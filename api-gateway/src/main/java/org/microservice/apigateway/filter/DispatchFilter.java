package org.microservice.apigateway.filter;

import org.microservice.apigateway.exception.SourceIllegalException;
import org.microservice.apigateway.service.KafkaService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Dispatch to different kafka topic
 * depend on source in the api path
 * Only applies to /api/v1/ingest/** requests
 */
@Component
@Order(2)
public class DispatchFilter implements GlobalFilter {

    private final KafkaService kafkaService;

    public DispatchFilter(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // only do to ingest
        if (!path.startsWith("/api/v1/ingest")) {
            return chain.filter(exchange);
        }

        String source = path.substring(path.lastIndexOf("/") + 1);
        if (source.isBlank()) {
            throw new SourceIllegalException("Missing Experience Source");
        }

        String tenantId = exchange.getAttribute("TENANT_ID");

        ServerHttpResponse response = exchange.getResponse();

        return request.getBody()
                .next()
                .flatMap(dataBuffer -> {
                    String body = dataBuffer.toString(StandardCharsets.UTF_8);
                    kafkaService.publish(body, source, tenantId);
                    response.setStatusCode(HttpStatus.ACCEPTED);
                    return response.setComplete();
                });
    }
}