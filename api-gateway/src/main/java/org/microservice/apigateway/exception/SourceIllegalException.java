package org.microservice.apigateway.exception;

public class SourceIllegalException extends RuntimeException {
    public SourceIllegalException(String message) {
        super(message);
    }
}
