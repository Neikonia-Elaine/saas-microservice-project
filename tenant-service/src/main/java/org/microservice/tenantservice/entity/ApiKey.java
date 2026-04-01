package org.microservice.tenantservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_hash", nullable = false, unique = true)
    private String keyHash;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;
}
