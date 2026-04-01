package org.microservice.analysisservice.dto;

import lombok.Data;
import java.util.List;

// Shape of the response from query-service GET /api/v1/admin/query/negative
@Data
public class StatsDto {
    private long totalRecords;          // all records in period
    private long negativeCount;         // negative records in period
    private double negativeRate;        // negativeCount / totalRecords * 100

    // Top store by absolute negative volume
    private String topVolumeStoreId;
    private long topVolumeStoreNegativeCount;
    private double topVolumeStoreNegativeRate;

    // Top store by negative rate (highest %, may be a small store with mostly bad records)
    private String highestRateStoreId;
    private double highestRateStorePct;

    // Ranked top 3 stores by negative count
    private List<StoreNegativeStat> top3Stores;
}
