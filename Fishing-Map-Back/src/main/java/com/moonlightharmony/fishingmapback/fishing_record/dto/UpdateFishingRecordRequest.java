package com.moonlightharmony.fishingmapback.fishing_record.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateFishingRecordRequest(
    @NotNull
    Long fishSpeciesId,

    @NotNull
    LocalDateTime caughtAt,

    @NotNull
    BigDecimal latitude,

    @NotNull
    BigDecimal longitude,

    @Size(max = 30)
    String region1DeptName,

    @Size(max = 30)
    String region2DeptName,

    @Size(max = 30)
    String region3DeptName,

    @Size(max = 1000)
    String comment,

    @NotNull
    FishingRecord.Visibility visibility,

    /**
     * 수정 후 최종 이미지 순서.
     * 기존 이미지는 id 문자열, 신규 자리는 빈 문자열(또는 null).
     * 빈 자리 개수는 newImages 크기와 일치해야 한다.
     */
    List<String> imageIds,

    List<MultipartFile> newImages
) {}
