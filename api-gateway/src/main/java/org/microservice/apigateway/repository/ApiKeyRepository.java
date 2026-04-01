package org.microservice.apigateway.repository;

import org.microservice.apigateway.entity.ApiKey;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ApiKeyRepository extends ReactiveCrudRepository<ApiKey, Long> {

    // Only returns a result if the key exists AND is not revoked
    Mono<ApiKey> findByKeyHashAndRevokedFalse(String keyHash);
}