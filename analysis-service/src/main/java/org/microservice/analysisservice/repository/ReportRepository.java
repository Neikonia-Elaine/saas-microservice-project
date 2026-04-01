package org.microservice.analysisservice.repository;

import org.microservice.analysisservice.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
