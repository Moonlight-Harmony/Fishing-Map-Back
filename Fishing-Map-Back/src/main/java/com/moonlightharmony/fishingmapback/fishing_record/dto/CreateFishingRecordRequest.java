package com.moonlightharmony.fishingmapback.fishing_record.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFishingRecordRequest(
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

    List<MultipartFile> images
) {}
