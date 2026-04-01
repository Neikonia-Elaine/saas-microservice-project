package org.microservice.queryservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class StatsDto {
    private long totalRecords;
    private long negativeCount;
    private double negativeRate;

    private String topVolumeStoreId;
    private long topVolumeStoreNegativeCount;
    private double topVolumeStoreNegativeRate;

    private String highestRateStoreId;
    private double highestRateStorePct;

    private List<StoreNegativeStat> top3Stores;
}
