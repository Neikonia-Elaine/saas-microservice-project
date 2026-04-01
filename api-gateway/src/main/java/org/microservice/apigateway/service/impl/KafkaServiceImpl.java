package org.microservice.apigateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.microservice.apigateway.service.KafkaService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String body, String source, String tenantId) {
        String topic = resolveTopic(source);

        Message<String> message = MessageBuilder
                .withPayload(body)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, tenantId) // partition
                .setHeader("X-Tenant-Id", tenantId)
                .build();

        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
//                        log.error("Failed to publish to Kafka topic={} tenantId={}: {}", topic, tenantId, ex.getMessage());
                    }
                });
    }

    private String resolveTopic(String source) {
        return switch (source) {
            case "order-rating"    -> "order-rating";
            case "survey-response" -> "survey-response";
            case "chat-feedback"   -> "chat-feedback";
            default -> throw new IllegalArgumentException("Unknown source: " + source);
        };
    }
}
