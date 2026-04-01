package org.microservice.tenantservice.service;

public interface JwtService {
    String generateToken(String email, String tenantId, String role);
}