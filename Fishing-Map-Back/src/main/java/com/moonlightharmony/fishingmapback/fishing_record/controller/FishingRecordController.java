package com.moonlightharmony.fishingmapback.fishing_record.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.moonlightharmony.fishingmapback.fishing_record.dto.CreateFishingRecordRequest;
import com.moonlightharmony.fishingmapback.fishing_record.dto.CreateFishingRecordResponse;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordDetailResponse;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordMarkerResponse;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordSummaryResponse;
import com.moonlightharmony.fishingmapback.fishing_record.service.FishingRecordService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fishing_record")
@RequiredArgsConstructor
public class FishingRecordController {
    
    private final FishingRecordService fishingRecordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateFishingRecordResponse create(
            @AuthenticationPrincipal String principal,
            @Valid @ModelAttribute CreateFishingRecordRequest request
    ) {
        Long userId = Long.valueOf(principal);
        return new CreateFishingRecordResponse(
                fishingRecordService.create(userId, request)
        );
    }

    @GetMapping("/markers")
    public List<FishingRecordMarkerResponse> findMarkers(
            @RequestParam String fishSpeciesName
    ) {
        return fishingRecordService.findMarkersByFishSpeciesName(fishSpeciesName);
    }

    @GetMapping("/{recordId}/summary")
    public FishingRecordSummaryResponse getSummary(@PathVariable Long recordId) {
        return fishingRecordService.getSummary(recordId);
    }

    @GetMapping("/{recordId}")
    public FishingRecordDetailResponse getDetail(@PathVariable Long recordId) {
        return fishingRecordService.getDetail(recordId);
    }
}
