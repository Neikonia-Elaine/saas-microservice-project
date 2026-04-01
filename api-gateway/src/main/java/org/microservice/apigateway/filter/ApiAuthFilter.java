package org.microservice.apigateway.filter;
import org.microservice.apigateway.exception.UnauthorizedException;
import org.microservice.apigateway.service.AuthService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class ApiAuthFilter implements GlobalFilter {

    private final AuthService authService;

    public ApiAuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Admin paths use JWT auth, not API key auth
        if (path.startsWith("/api/v1/admin")) {
            return chain.filter(exchange);
        }

        String apiKey = request.getHeaders().getFirst("X-API-Key");
        if (apiKey == null || apiKey.isBlank()) {
            return Mono.error(new UnauthorizedException("Missing API key"));
        }

        return authService.validateApiKey(apiKey)
                .flatMap(tenantId -> {
                    exchange.getAttributes().put("TENANT_ID", tenantId);
                    return chain.filter(exchange);
                });
    }
}
