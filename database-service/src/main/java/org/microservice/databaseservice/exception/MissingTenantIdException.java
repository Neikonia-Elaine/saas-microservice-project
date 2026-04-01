package org.microservice.databaseservice.exception;

public class MissingTenantIdException extends RuntimeException {
    public MissingTenantIdException() {
        super("Missing required header: X-Tenant-Id");
    }
}
