package org.microservice.analysisservice.controller;

import org.microservice.analysisservice.entity.Report;
import org.microservice.analysisservice.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports")
public class AnalysisController {

    private final ReportService reportService;

    public AnalysisController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Report> generate(@RequestParam String tenantId,
                                           @RequestParam String from,
                                           @RequestParam String to) {
        Report report = reportService.generateReport(tenantId, from, to);
        return ResponseEntity.ok(report);
    }
}
