package org.microservice.databaseservice.repository;

import org.microservice.databaseservice.entity.ChatFeedbackEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatFeedbackEventRepository extends JpaRepository<ChatFeedbackEvent, String> {
}
