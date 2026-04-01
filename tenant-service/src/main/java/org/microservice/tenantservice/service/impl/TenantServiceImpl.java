package org.microservice.tenantservice.service.impl;

import org.microservice.tenantservice.entity.ApiKey;
import org.microservice.tenantservice.entity.Tenant;
import org.microservice.tenantservice.exception.TenantNotFoundException;
import org.microservice.tenantservice.repository.ApiKeyRepository;
import org.microservice.tenantservice.repository.TenantRepository;
import org.microservice.tenantservice.service.TenantService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final ApiKeyRepository apiKeyRepository;

    public TenantServiceImpl(TenantRepository tenantRepository, ApiKeyRepository apiKeyRepository) {
        this.tenantRepository = tenantRepository;
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public Tenant createTenant(String name) {
        Tenant tenant = new Tenant();
        tenant.setTenantId(UUID.randomUUID().toString());
        tenant.setName(name);
        return tenantRepository.save(tenant);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public boolean isActive(String tenantId) {
        Optional<Tenant> tenant = tenantRepository.findByTenantId(tenantId);
        Tenant found = tenant.orElseThrow(() -> new TenantNotFoundException(tenantId));
        return found.isActive();
    }

    @Override
    public String generateApiKey(String tenantId) {
        tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new TenantNotFoundException(tenantId));

        String rawKey = "sk_live_" + UUID.randomUUID().toString().replace("-", "");

        ApiKey apiKey = new ApiKey();
        apiKey.setTenantId(tenantId);
        apiKey.setKeyHash(hash(rawKey));
        apiKeyRepository.save(apiKey);

        return rawKey;
    }

    private String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
