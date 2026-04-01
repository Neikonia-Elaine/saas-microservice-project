package org.microservice.apigateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Global exception handler for the reactive gateway.
 * Must be @Order(-2) to run before Spring Boot's DefaultErrorWebExceptionHandler (-1).
 */
@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String message;

        if (ex instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
            message = ex.getMessage();
        } else if (ex instanceof SourceIllegalException) {
            status = HttpStatus.BAD_REQUEST;
            message = ex.getMessage();
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            message = ex.getMessage();
        } else {
            log.error("Unhandled gateway exception: {}", ex.getClass().getName(), ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal server error";
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"error\": \"%s\"}", message);
        DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
