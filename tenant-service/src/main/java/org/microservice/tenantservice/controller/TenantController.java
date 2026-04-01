package org.microservice.tenantservice.controller;

import org.microservice.tenantservice.dto.request.CreateTenantRequest;
import org.microservice.tenantservice.entity.Tenant;
import org.microservice.tenantservice.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping("/tenant")
    public ResponseEntity<Tenant> createTenant(
            @RequestBody CreateTenantRequest request,
            @RequestHeader("X-Role") String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
        Tenant tenant = tenantService.createTenant(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
    }

    @GetMapping("/tenants")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @PostMapping("/{tenantId}/apiKey")
    public ResponseEntity<Map<String, String>> generateApiKey(
            @PathVariable String tenantId,
            @RequestHeader("X-Role") String role,
            @RequestHeader(value = "X-Tenant-Id", required = false) String jwtTenantId) {
        if (!"ROLE_ADMIN".equals(role) && !tenantId.equals(jwtTenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tenant ID mismatch");
        }
        String rawKey = tenantService.generateApiKey(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("apiKey", rawKey));
    }
}
