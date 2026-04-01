package org.microservice.apigateway.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("api_keys")
public class ApiKey {

    @Id
    private Long id;

    @Column("key_hash")
    private String keyHash;

    @Column("tenant_id")
    private String tenantId;

    @Column("revoked")
    private Boolean revoked;
}