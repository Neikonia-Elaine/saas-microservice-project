package org.microservice.tenantservice.controller;

import org.microservice.tenantservice.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 内部服务间调用专用，不经过 Gateway，不需要鉴权
@RestController
@RequestMapping("/internal/tenants")
public class InternalTenantController {

    private final TenantService tenantService;

    public InternalTenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/{tenantId}/active")
    public ResponseEntity<Boolean> isActive(@PathVariable String tenantId) {
        boolean active = tenantService.isActive(tenantId);
        return ResponseEntity.ok(active);
    }
}
