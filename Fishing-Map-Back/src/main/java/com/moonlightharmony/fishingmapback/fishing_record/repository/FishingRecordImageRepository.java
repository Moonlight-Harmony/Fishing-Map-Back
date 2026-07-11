package com.moonlightharmony.fishingmapback.fishing_record.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecordImage;

public interface FishingRecordImageRepository extends JpaRepository<FishingRecordImage, Long> {

    FishingRecordImage findByFishingRecordIdInAndDisplayOrder(Long id, int displayOrder);

    List<FishingRecordImage> findByFishingRecordIdOrderByDisplayOrderAsc(Long fishingRecordId);
}
