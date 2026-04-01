package org.microservice.databaseservice.service;

public interface EventService {
    void save(String tenantId, String eventType, String data);
}
