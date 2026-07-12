package com.moonlightharmony.fishingmapback.fishing_record.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecordImage;

public interface FishingRecordImageRepository extends JpaRepository<FishingRecordImage, Long> {

    Optional<FishingRecordImage> findByFishingRecordIdAndDisplayOrder(Long id, int displayOrder);

    List<FishingRecordImage> findByFishingRecordIdOrderByDisplayOrderAsc(Long fishingRecordId);
}
