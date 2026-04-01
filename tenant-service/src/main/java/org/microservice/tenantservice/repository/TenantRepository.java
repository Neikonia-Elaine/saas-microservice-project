package org.microservice.tenantservice.repository;

import org.microservice.tenantservice.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    boolean existsByTenantId(String tenantId);

    Optional<Tenant> findByTenantId(String tenantId);
}
