package com.moonlightharmony.fishingmapback.fishing_record.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;

public interface FishingRecordRepository extends JpaRepository<FishingRecord, Long> {

    Optional<FishingRecord> findByIdAndDeletedAtIsNull(Long id);

    List<FishingRecord> findByFishSpeciesIdAndDeletedAtIsNull(Long fishSpeciesId);

    List<FishingRecord> findByRegion1DeptNameAndDeletedAtIsNull(String region1DeptName);

    List<FishingRecord> findByRegion1DeptNameAndRegion2DeptNameAndDeletedAtIsNull(
        String region1DeptName, String region2DeptName);

    List<FishingRecord> findByRegion1DeptNameAndRegion2DeptNameAndRegion3DeptNameAndDeletedAtIsNull(
        String region1DeptName, String region2DeptName, String region3DeptName);
}
