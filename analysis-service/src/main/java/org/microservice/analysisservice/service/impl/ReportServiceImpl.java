package org.microservice.analysisservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.microservice.analysisservice.dto.StatsDto;
import org.microservice.analysisservice.dto.StoreNegativeStat;
import org.microservice.analysisservice.entity.Report;
import org.microservice.analysisservice.repository.ReportRepository;
import org.microservice.analysisservice.service.ReportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    private static final String SYSTEM_PROMPT = """
            You are a retail operations analyst writing an internal business report.
            You will be given negative feedback/incident statistics for a store network over two periods.
            Write a professional 3-paragraph analysis covering:
            1. Current situation: overall negative record volume, rate severity, and what it means operationally.
            2. Trend: whether the situation is improving or deteriorating vs the previous period, and the magnitude of change.
            3. Store-level risk: identify the highest-risk stores by volume and by rate, and suggest concrete follow-up actions.
            Be specific with the numbers provided. Do not fabricate data beyond what is given.
            """;

    private final RestTemplate restTemplate;
    private final ReportRepository reportRepository;
    private final WebClient openAiWebClient;
    private final String model;

    public ReportServiceImpl(RestTemplate restTemplate,
                             ReportRepository reportRepository,
                             WebClient openAiWebClient,
                             @Value("${openai.model}") String model) {
        this.restTemplate = restTemplate;
        this.reportRepository = reportRepository;
        this.openAiWebClient = openAiWebClient;
        this.model = model;
    }

    @Override
    public Report generateReport(String tenantId, String from, String to) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        long periodDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1;

        // key components for report
        LocalDate prevTo = fromDate.minusDays(1);
        LocalDate prevFrom = prevTo.minusDays(periodDays - 1);
        String prevFromStr = prevFrom.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String prevToStr = prevTo.format(DateTimeFormatter.ISO_LOCAL_DATE);

        StatsDto current = fetchNegativeStats(tenantId, from, to);
        StatsDto previous = fetchNegativeStats(tenantId, prevFromStr, prevToStr);

        double rateChangePct = previous.getNegativeCount() == 0 ? 0
                : (double) (current.getNegativeCount() - previous.getNegativeCount())
                  / previous.getNegativeCount() * 100;
        String trend = rateChangePct > 2 ? "RISING" : rateChangePct < -2 ? "FALLING" : "STABLE";
        String changeArrow = rateChangePct > 0 ? "↑" : rateChangePct < 0 ? "↓" : "→";

        // prompt
        String userMessage = buildUserMessage(
                tenantId, from, to, prevFromStr, prevToStr,
                current, previous, rateChangePct, trend, changeArrow
        );
        String analysisText = callOpenAi(userMessage);

        // save
        Report report = new Report();
        report.setTenantId(tenantId);
        report.setTitle("Negative Record Analysis: " + from + " ~ " + to);
        report.setReport(analysisText);
        return reportRepository.save(report);
    }

    // call query service for data
    private StatsDto fetchNegativeStats(String tenantId, String from, String to) {
        return restTemplate.getForObject(
                "http://QUERY-SERVICE/api/v1/admin/query/negative?tenantId={tenantId}&from={from}&to={to}",
                StatsDto.class, tenantId, from, to);
    }

    private String buildUserMessage(String tenantId,
                                    String from, String to,
                                    String prevFrom, String prevTo,
                                    StatsDto current, StatsDto previous,
                                    double rateChangePct, String trend, String changeArrow) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Negative Record Analysis ===\n");
        sb.append("Tenant          : ").append(tenantId).append("\n");
        sb.append("Analysis Period : ").append(from).append(" to ").append(to).append("\n");
        sb.append("Comparison Period: ").append(prevFrom).append(" to ").append(prevTo).append("\n\n");

        sb.append("--- Current Period ---\n");
        sb.append(String.format("Total Records    : %,d%n", current.getTotalRecords()));
        sb.append(String.format("Negative Records : %,d (%.2f%% of total)%n",
                current.getNegativeCount(), current.getNegativeRate()));

        sb.append("\n--- Trend vs Previous Period ---\n");
        sb.append(String.format("Previous Negative Records: %,d%n", previous.getNegativeCount()));
        sb.append(String.format("Change: %+.0f records (%+.1f%%) %s %s%n",
                (double) (current.getNegativeCount() - previous.getNegativeCount()),
                rateChangePct, changeArrow, trend));

        sb.append("\n--- Store Risk Breakdown ---\n");
        sb.append(String.format("Highest Volume : %s  (%,d negatives, %.1f%% of its own transactions)%n",
                current.getTopVolumeStoreId(),
                current.getTopVolumeStoreNegativeCount(),
                current.getTopVolumeStoreNegativeRate()));
        sb.append(String.format("Highest Rate   : %s  (%.1f%% negative rate — highest in network)%n",
                current.getHighestRateStoreId(),
                current.getHighestRateStorePct()));

        if (current.getTop3Stores() != null && !current.getTop3Stores().isEmpty()) {
            sb.append("\nTop 3 Stores by Negative Volume:\n");
            int rank = 1;
            for (StoreNegativeStat s : current.getTop3Stores()) {
                sb.append(String.format(
                        "  %d. %s : %,d negatives | negative rate %.1f%% | %,d total records (%.1f%% of tenant volume)%n",
                        rank++,
                        s.getStoreId(),
                        s.getNegativeCount(),
                        s.getNegativeRate(),
                        s.getStoreTotalRecords(),
                        s.getTenantSharePct()));
            }
        }

        return sb.toString();
    }

    private String callOpenAi(String userMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        Map<?, ?> response = openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<?> choices = (List<?>) response.get("choices");
        Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
        return (String) message.get("content");
    }
}
