package com.moonlightharmony.fishingmapback.fishing_record.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;

public interface FishingRecordRepository extends JpaRepository<FishingRecord, Long> {
    
    List<FishingRecord> findByFishSpeciesId(Long fishSpeciesId);
}
