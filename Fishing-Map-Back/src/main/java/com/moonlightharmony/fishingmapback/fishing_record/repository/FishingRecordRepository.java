package com.moonlightharmony.fishingmapback.fishing_record.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;

public interface FishingRecordRepository extends JpaRepository<FishingRecord, Long> {
    
    Optional<FishingRecord> findByIdAndDeletedAtIsNull(Long id);

    List<FishingRecord> findByFishSpeciesIdAndDeletedAtIsNull(Long fishSpeciesId);
}
