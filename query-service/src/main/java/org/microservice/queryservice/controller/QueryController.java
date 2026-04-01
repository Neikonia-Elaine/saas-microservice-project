package org.microservice.queryservice.controller;

import org.microservice.queryservice.dto.StatsDto;
import org.microservice.queryservice.service.QueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/query")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/negative")
    public StatsDto queryNegative(@RequestParam String tenantId,
                                  @RequestParam String from,
                                  @RequestParam String to) {
        return queryService.queryNegativeData(tenantId, from, to);
    }
}
