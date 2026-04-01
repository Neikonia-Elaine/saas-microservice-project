package org.microservice.analysisservice.dto;

import lombok.Data;

@Data
public class StoreNegativeStat {
    private String storeId;
    private long negativeCount;
    private double negativeRate;    // negatives / store's own total records * 100
    private long storeTotalRecords; // store's total records in the period
    private double tenantSharePct;  // store's total records / tenant total records * 100
}
