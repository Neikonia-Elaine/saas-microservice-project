package org.microservice.ingestservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.microservice.ingestservice.client.TenantServiceClient;
import org.microservice.ingestservice.service.IngestService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class IngestServiceImpl implements IngestService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final TenantServiceClient tenantServiceClient;

    public IngestServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper, TenantServiceClient tenantServiceClient) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.tenantServiceClient = tenantServiceClient;
    }

    @Override
    public void saveEvent(String payload, String source, String tenantId) {
        // check if the tenant is valid
        tenantServiceClient.validateActive(tenantId);

        // take type from kafka topic
        ObjectNode node;
        try {
            node = (ObjectNode) objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse event payload", e);
        }
        node.put("type", source);

        // enrich datas
        enrichAttitude(node, source);

        String enrichedPayload;
        try {
            enrichedPayload = objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event payload", e);
        }

        // save via database service
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", tenantId);
        headers.set("X-Source", source);

        HttpEntity<String> request = new HttpEntity<>(enrichedPayload, headers);
        restTemplate.postForObject("http://DATABASE-SERVICE/data", request, Void.class);
    }

    // order-rating: 1~3 → negative, 4~5 → positive
    // chat-feedback: call AI service - todo
    private void enrichAttitude(ObjectNode node, String source) {
        switch (source) {
            case "order-rating" -> {
                int rating = node.path("rating").asInt(0);
                node.put("attitude", rating <= 3 ? "negative" : "positive");
            }
            case "chat-feedback" -> {
                // TODO: call ANALYSIS-SERVICE to classify chat content
                // String content = node.path("content").asText("");
                // String attitude = restTemplate.postForObject(
                //         "http://ANALYSIS-SERVICE/attitude", content, String.class);
                // node.put("attitude", attitude);
            }
        }
    }
}
