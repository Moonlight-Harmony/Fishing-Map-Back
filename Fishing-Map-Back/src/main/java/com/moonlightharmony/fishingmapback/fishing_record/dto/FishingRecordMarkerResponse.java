package com.moonlightharmony.fishingmapback.fishing_record.dto;

import java.math.BigDecimal;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;

public record FishingRecordMarkerResponse(
    Long id,
    BigDecimal latitude,
    BigDecimal longitude
) {
    public static FishingRecordMarkerResponse from(FishingRecord fishingRecord) {
        return new FishingRecordMarkerResponse(
                fishingRecord.getId(),
                fishingRecord.getLatitude(),
                fishingRecord.getLongitude()
        );
    }
}
