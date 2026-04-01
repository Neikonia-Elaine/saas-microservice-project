package org.microservice.databaseservice.repository;

import org.microservice.databaseservice.entity.SurveyResponseEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResponseEventRepository extends JpaRepository<SurveyResponseEvent, String> {
}
