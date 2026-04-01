package org.microservice.tenantservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", unique = true, nullable = false)
    private String tenantId; // UUID, generated on creation, shared across all services

    @Column(name = "name", nullable = false)
    private String name;

    // 账户是否有效；禁用后 ingest-service 会拒绝该 tenant 的事件
    @Column(nullable = false)
    private boolean active = true;
}
