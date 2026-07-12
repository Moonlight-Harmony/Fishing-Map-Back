package com.moonlightharmony.fishingmapback.fishing_record.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;

public record FishingRecordDetailResponse(
    Long id,
    String fishSpeciesName,
    LocalDateTime caughtAt,
    BigDecimal latitude,
    BigDecimal longitude,
    String region1DeptName,
    String region2DeptName,
    String region3DeptName,
    String comment,
    FishingRecord.Visibility visibility,
    String nickname,
    List<String> imageUrls
) {
    public static FishingRecordDetailResponse from(
            FishingRecord fishingRecord,
            List<String> imageUrls
    ) {
        return new FishingRecordDetailResponse(
                fishingRecord.getId(), 
                fishingRecord.getFishSpecies().getName(),
                fishingRecord.getCaughtAt(),
                fishingRecord.getLatitude(), 
                fishingRecord.getLongitude(),
                fishingRecord.getRegion1DeptName(),
                fishingRecord.getRegion2DeptName(),
                fishingRecord.getRegion3DeptName(),
                fishingRecord.getComment(),
                fishingRecord.getVisibility(),
                fishingRecord.getUser().getNickname(),
                imageUrls
        );
    }
}
