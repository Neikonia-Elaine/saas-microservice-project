package org.microservice.queryservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_rating")
public class OrderRatingEvent {

    @Id
    private String id;

    private String tenantId;

    private String storeId;

    private String orderId;

    private String customerId;

    private Integer rating;

    private String attitude;

    private LocalDateTime createdAt;
}
