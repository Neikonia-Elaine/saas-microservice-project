package org.microservice.tenantservice.service;

import org.microservice.tenantservice.entity.Tenant;

import java.util.List;

public interface TenantService {

    Tenant createTenant(String name);

    List<Tenant> getAllTenants();

    boolean isActive(String tenantId);

    String generateApiKey(String tenantId);
}
