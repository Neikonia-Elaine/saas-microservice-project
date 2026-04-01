package org.microservice.queryservice.service.impl;

import org.microservice.queryservice.dto.StatsDto;
import org.microservice.queryservice.dto.StoreNegativeStat;
import org.microservice.queryservice.repository.OrderRatingEventRepository;
import org.microservice.queryservice.service.QueryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
public class QueryServiceImpl implements QueryService {

    private final OrderRatingEventRepository repo;

    public QueryServiceImpl(OrderRatingEventRepository repo) {
        this.repo = repo;
    }

    @Override
    public StatsDto queryNegativeData(String tenantId, String from, String to) {
        LocalDateTime fromDt = LocalDate.parse(from).atStartOfDay();
        LocalDateTime toDt   = LocalDate.parse(to).atTime(LocalTime.MAX);

        long total    = repo.countTotal(tenantId, fromDt, toDt);
        long negative = repo.countNegative(tenantId, fromDt, toDt);

        // [storeId, totalCount, negativeCount]
        List<Object[]> rows = repo.findStoreStats(tenantId, fromDt, toDt);

        List<StoreNegativeStat> storeStats = rows.stream().map(r -> {
            StoreNegativeStat s = new StoreNegativeStat();
            s.setStoreId((String) r[0]);
            long storeTotal    = ((Number) r[1]).longValue();
            long storeNegative = ((Number) r[2]).longValue();
            s.setStoreTotalRecords(storeTotal);
            s.setNegativeCount(storeNegative);
            s.setNegativeRate(storeTotal == 0 ? 0.0 : (double) storeNegative / storeTotal * 100);
            s.setTenantSharePct(total == 0 ? 0.0 : (double) storeTotal / total * 100);
            return s;
        }).toList();

        // Top 3 by negative volume
        List<StoreNegativeStat> top3 = storeStats.stream()
                .sorted(Comparator.comparingLong(StoreNegativeStat::getNegativeCount).reversed())
                .limit(3)
                .toList();

        // Top by absolute negative volume
        StoreNegativeStat topVolume = storeStats.stream()
                .max(Comparator.comparingLong(StoreNegativeStat::getNegativeCount))
                .orElse(null);

        // Top by negative rate
        StoreNegativeStat topRate = storeStats.stream()
                .max(Comparator.comparingDouble(StoreNegativeStat::getNegativeRate))
                .orElse(null);

        StatsDto dto = new StatsDto();
        dto.setTotalRecords(total);
        dto.setNegativeCount(negative);
        dto.setNegativeRate(total == 0 ? 0.0 : (double) negative / total * 100);

        if (topVolume != null) {
            dto.setTopVolumeStoreId(topVolume.getStoreId());
            dto.setTopVolumeStoreNegativeCount(topVolume.getNegativeCount());
            dto.setTopVolumeStoreNegativeRate(topVolume.getNegativeRate());
        }
        if (topRate != null) {
            dto.setHighestRateStoreId(topRate.getStoreId());
            dto.setHighestRateStorePct(topRate.getNegativeRate());
        }
        dto.setTop3Stores(top3);

        return dto;
    }
}
