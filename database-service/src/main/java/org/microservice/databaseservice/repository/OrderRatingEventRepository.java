package org.microservice.databaseservice.repository;

import org.microservice.databaseservice.entity.OrderRatingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRatingEventRepository extends JpaRepository<OrderRatingEvent, String> {
}
