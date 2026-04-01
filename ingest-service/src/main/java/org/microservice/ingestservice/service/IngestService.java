package org.microservice.ingestservice.service;

public interface IngestService {

    void saveEvent(String payload, String source, String tenantId);
}
