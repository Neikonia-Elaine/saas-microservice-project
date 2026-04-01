package org.microservice.databaseservice.controller;

import org.microservice.databaseservice.exception.MissingTenantIdException;
import org.microservice.databaseservice.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ingest-service: POST /data
    // Header: X-Tenant-Id, X-Source
    // Body: raw JSON string (e.g. {"rating":3,"orderID":7263987,...,"type":"order-rating"})
    @PostMapping("/data")
    public ResponseEntity<Void> saveEvent(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader("X-Source") String eventType,
            @RequestBody String data) {

        if (tenantId == null || tenantId.isBlank()) {
            throw new MissingTenantIdException();
        }

        eventService.save(tenantId, eventType, data);
        return ResponseEntity.ok().build();
    }
}
