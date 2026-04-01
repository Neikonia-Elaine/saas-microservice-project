package org.microservice.queryservice.service;

import org.microservice.queryservice.dto.StatsDto;

public interface QueryService {
    StatsDto queryNegativeData(String tenantId, String from, String to);
}
