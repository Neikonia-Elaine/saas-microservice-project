package org.microservice.apigateway.service;

import reactor.core.publisher.Mono;

public interface AuthService {

    // Validates key (exists + not revoked) and returns tenantId, or errors with UnauthorizedException
    Mono<String> validateApiKey(String rawApiKey);
}