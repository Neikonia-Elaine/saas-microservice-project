package org.microservice.queryservice.dto;

import lombok.Data;

@Data
public class StoreNegativeStat {
    private String storeId;
    private long negativeCount;
    private double negativeRate;        // negatives / store's own total * 100
    private long storeTotalRecords;
    private double tenantSharePct;      // store's total / tenant total * 100
}
