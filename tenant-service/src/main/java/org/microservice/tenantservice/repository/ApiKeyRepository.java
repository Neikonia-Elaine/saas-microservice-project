package org.microservice.tenantservice.repository;

import org.microservice.tenantservice.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
}
