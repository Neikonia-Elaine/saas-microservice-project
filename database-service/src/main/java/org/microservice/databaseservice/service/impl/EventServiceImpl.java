package org.microservice.databaseservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.microservice.databaseservice.entity.ChatFeedbackEvent;
import org.microservice.databaseservice.entity.OrderRatingEvent;
import org.microservice.databaseservice.entity.SurveyResponseEvent;
import org.microservice.databaseservice.repository.ChatFeedbackEventRepository;
import org.microservice.databaseservice.repository.OrderRatingEventRepository;
import org.microservice.databaseservice.repository.SurveyResponseEventRepository;
import org.microservice.databaseservice.service.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

    private final OrderRatingEventRepository orderRatingRepo;
    private final SurveyResponseEventRepository surveyResponseRepo;
    private final ChatFeedbackEventRepository chatFeedbackRepo;
    private final ObjectMapper objectMapper;

    public EventServiceImpl(OrderRatingEventRepository orderRatingRepo,
                            SurveyResponseEventRepository surveyResponseRepo,
                            ChatFeedbackEventRepository chatFeedbackRepo,
                            ObjectMapper objectMapper) {
        this.orderRatingRepo = orderRatingRepo;
        this.surveyResponseRepo = surveyResponseRepo;
        this.chatFeedbackRepo = chatFeedbackRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(String tenantId, String eventType, String data) {
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        switch (eventType) {
            case "order-rating"    -> saveOrderRating(id, tenantId, data, now);
            case "survey-response" -> saveSurveyResponse(id, tenantId, now);
            case "chat-feedback"   -> saveChatFeedback(id, tenantId, now);
            default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
        }
    }

    private void saveOrderRating(String id, String tenantId, String data, LocalDateTime now) {
        JsonNode node;
        try {
            node = objectMapper.readTree(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse order-rating payload", e);
        }

        OrderRatingEvent event = new OrderRatingEvent();
        event.setId(id);
        event.setTenantId(tenantId);
        event.setStoreId(node.path("storeId").asText(null));
        event.setOrderId(node.path("orderId").asText(null));
        event.setCustomerId(node.path("customerId").asText(null));
        event.setRating(node.path("rating").asInt());
        event.setAttitude(node.path("attitude").asText(null));
        event.setCreatedAt(now);
        orderRatingRepo.save(event);
    }

    private void saveSurveyResponse(String id, String tenantId, LocalDateTime now) {
        SurveyResponseEvent event = new SurveyResponseEvent();
        event.setId(id);
        event.setTenantId(tenantId);
        event.setCreatedAt(now);
        surveyResponseRepo.save(event);
    }

    private void saveChatFeedback(String id, String tenantId, LocalDateTime now) {
        ChatFeedbackEvent event = new ChatFeedbackEvent();
        event.setId(id);
        event.setTenantId(tenantId);
        event.setCreatedAt(now);
        chatFeedbackRepo.save(event);
    }
}
