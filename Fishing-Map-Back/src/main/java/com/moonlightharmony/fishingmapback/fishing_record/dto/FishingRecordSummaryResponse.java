package com.moonlightharmony.fishingmapback.fishing_record.dto;

import java.time.LocalDateTime;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;

public record FishingRecordSummaryResponse(
    Long id,
    String fishSpeciesName,
    LocalDateTime caughtAt,
    String region1DeptName,
    String region2DeptName,
    String region3DeptName,
    String nickname,
    String thumbnailImageUrl
) {
    public static FishingRecordSummaryResponse from(
            FishingRecord fishingRecord, 
            String thumbnailImageUrl
    ) {
        return new FishingRecordSummaryResponse(
                fishingRecord.getId(),
                fishingRecord.getFishSpecies().getName(),
                fishingRecord.getCaughtAt(),
                fishingRecord.getRegion1DeptName(),
                fishingRecord.getRegion2DeptName(),
                fishingRecord.getRegion3DeptName(),
                fishingRecord.getUser().getNickname(),
                thumbnailImageUrl
        );
    }
}
