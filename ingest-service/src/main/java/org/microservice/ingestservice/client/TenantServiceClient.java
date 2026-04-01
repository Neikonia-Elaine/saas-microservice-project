package org.microservice.ingestservice.client;

import org.microservice.ingestservice.exception.InactiveTenantException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TenantServiceClient {

    private final RestTemplate restTemplate;

    public TenantServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void validateActive(String tenantId) {
        String url = "http://TENANT-SERVICE/internal/tenants/" + tenantId + "/active";
        Boolean active = restTemplate.getForObject(url, Boolean.class);
        if (!Boolean.TRUE.equals(active)) {
            throw new InactiveTenantException(tenantId);
        }
    }
}
