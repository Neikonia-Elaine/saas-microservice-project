package org.microservice.analysisservice.service;

import org.microservice.analysisservice.entity.Report;

public interface ReportService {
    Report generateReport(String tenantId, String from, String to);
}
