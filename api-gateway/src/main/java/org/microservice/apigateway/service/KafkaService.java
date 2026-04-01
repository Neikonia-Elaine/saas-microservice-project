package org.microservice.apigateway.service;

public interface KafkaService {
    void publish(String body, String source, String tenantId);
}
