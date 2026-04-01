package org.microservice.apigateway.service.impl;

import org.microservice.apigateway.entity.ApiKey;
import org.microservice.apigateway.exception.UnauthorizedException;
import org.microservice.apigateway.repository.ApiKeyRepository;
import org.microservice.apigateway.service.AuthService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class AuthServiceImpl implements AuthService {

    private final ApiKeyRepository apiKeyRepository;

    public AuthServiceImpl(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public Mono<String> validateApiKey(String rawApiKey) {
        String hashedKey = hashApiKey(rawApiKey);
        return apiKeyRepository.findByKeyHashAndRevokedFalse(hashedKey)
                .switchIfEmpty(Mono.error(new UnauthorizedException("Invalid or revoked API key")))
                .map(ApiKey::getTenantId);
    }

    private String hashApiKey(String rawApiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawApiKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}