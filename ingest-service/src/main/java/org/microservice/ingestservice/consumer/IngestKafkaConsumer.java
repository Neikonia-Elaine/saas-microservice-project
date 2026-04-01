package org.microservice.ingestservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.microservice.ingestservice.service.IngestService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IngestKafkaConsumer {

    private final IngestService ingestService;

    public IngestKafkaConsumer(IngestService ingestService) {
        this.ingestService = ingestService;
    }

    @KafkaListener(topics = {"order-rating", "survey-response", "chat-feedback"})
    public void consume(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String tenantId) {

        log.info("Received event: topic={}, tenantId={}", topic, tenantId);
        ingestService.saveEvent(payload, topic, tenantId);
    }
}
