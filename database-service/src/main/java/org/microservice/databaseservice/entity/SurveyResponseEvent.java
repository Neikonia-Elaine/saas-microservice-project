package org.microservice.databaseservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "survey_response")
public class SurveyResponseEvent {

    @Id
    private String id;

    private String tenantId;

    private LocalDateTime createdAt;
}
