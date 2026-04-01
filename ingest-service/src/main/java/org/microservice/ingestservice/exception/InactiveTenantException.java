package org.microservice.ingestservice.exception;

public class InactiveTenantException extends RuntimeException {
    public InactiveTenantException(String tenantId) {
        super("Tenant is inactive: " + tenantId);
    }
}
