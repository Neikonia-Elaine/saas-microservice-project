package org.microservice.analysisservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String tenantId;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String report;
}
