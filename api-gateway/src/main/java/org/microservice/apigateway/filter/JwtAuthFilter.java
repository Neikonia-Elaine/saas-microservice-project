package org.microservice.apigateway.filter;

import org.microservice.apigateway.exception.UnauthorizedException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * JWT authentication filter
 * Only applies to /api/v1/admin/** requests
 * Skips: login, user registration, admin registration, admin login
 */
@Component
@Order(1)
public class JwtAuthFilter implements GlobalFilter {

    private static final String SECRET = "J5bTVqv5h2RREWAqLshZ+f2oWG8IqirEcRYLAFI8nXI=";

    private static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(SECRET.getBytes());
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // skip ingest
        if (!path.startsWith("/api/v1/admin")) {
            return chain.filter(exchange);
        }

        // skip login
        if (path.equals("/api/v1/admin/login")) {
            return chain.filter(exchange);
        }

        // skip user registration
        if (path.equals("/api/v1/admin/users") && HttpMethod.POST.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        // skip admin registration and admin login
        if (path.equals("/api/v1/admin/admin") || path.equals("/api/v1/admin/admin/login")) {
            return chain.filter(exchange);
        }

        // get token
        HttpHeaders headers = request.getHeaders();
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing JWT token");
        }

        String token = authHeader.substring(7);

        Claims claims = validateToken(token);

        String userId = claims.getSubject();
        String tenantId = claims.get("tenantId", String.class);
        String role = claims.get("role", String.class);

        exchange.getAttributes().put("USER_ID", userId);
        if (tenantId != null) {
            exchange.getAttributes().put("TENANT_ID", tenantId);
        }
        exchange.getAttributes().put("ROLE", role);

        ServerHttpRequest.Builder mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", userId)
                .header("X-Role", role);

        if (tenantId != null) {
            mutatedRequest.header("X-Tenant-Id", tenantId);
        }

        return chain.filter(exchange.mutate().request(mutatedRequest.build()).build());
    }

    private Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(generalKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid JWT token");
        }
    }
}
