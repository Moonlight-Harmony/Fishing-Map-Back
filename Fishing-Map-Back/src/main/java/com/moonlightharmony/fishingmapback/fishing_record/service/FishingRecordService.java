package com.moonlightharmony.fishingmapback.fishing_record.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moonlightharmony.fishingmapback.exception.AppException;
import com.moonlightharmony.fishingmapback.exception.ErrorCode;
import com.moonlightharmony.fishingmapback.fish_species.entity.FishSpecies;
import com.moonlightharmony.fishingmapback.fish_species.repository.FishSpeciesRepository;
import com.moonlightharmony.fishingmapback.fishing_record.dto.CreateFishingRecordRequest;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordDetailResponse;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordMarkerResponse;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordSummaryResponse;
import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;
import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecordImage;
import com.moonlightharmony.fishingmapback.fishing_record.repository.FishingRecordImageRepository;
import com.moonlightharmony.fishingmapback.fishing_record.repository.FishingRecordRepository;
import com.moonlightharmony.fishingmapback.storage.FileStore;
import com.moonlightharmony.fishingmapback.storage.StorageLocation;
import com.moonlightharmony.fishingmapback.storage.StoragePathResolver;
import com.moonlightharmony.fishingmapback.user.entity.User;
import com.moonlightharmony.fishingmapback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FishingRecordService {
    
    private final FishingRecordRepository fishingRecordRepository;
    private final UserRepository userRepository;
    private final FishSpeciesRepository fishSpeciesRepository;
    private final FishingRecordImageRepository fishingRecordImageRepository;
    private final FileStore fileStore;
    private final StoragePathResolver pathResolver;

    @Transactional
    public Long create(Long userId, CreateFishingRecordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        FishSpecies fishSpecies = fishSpeciesRepository.findById(request.fishSpeciesId())
                .orElseThrow(() -> new AppException(ErrorCode.FISH_SPECIES_NOT_FOUND));

        FishingRecord fishingRecord = FishingRecord.builder()
                .user(user)
                .fishSpecies(fishSpecies)
                .longitude(request.longitude())
                .latitude(request.latitude())
                .region1DeptName(request.region1DeptName())
                .region2DeptName(request.region2DeptName())
                .region3DeptName(request.region3DeptName())
                .comment(request.comment())
                .visibility(request.visibility())
                .caughtAt(request.caughtAt())
                .build();

        FishingRecord savedFishingRecord = fishingRecordRepository.save(fishingRecord);

        List<String> storedFileNames = fileStore.storeFiles(request.images(), StorageLocation.FISHING_RECORD_IMAGE);

        for (int i=0; i<storedFileNames.size(); i++) {
            fishingRecordImageRepository.save(
                    FishingRecordImage.builder()
                            .fishingRecord(savedFishingRecord)
                            .storedFilename(storedFileNames.get(i))
                            .displayOrder(i)
                            .build()
            );
        }

        return savedFishingRecord.getId();
    }

    @Transactional(readOnly = true)
    public List<FishingRecordMarkerResponse> findMarkersByFishSpeciesName(String name) {
        FishSpecies fishSpecies = fishSpeciesRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.FISH_SPECIES_NOT_FOUND));
        
        List<FishingRecord> fishingRecords = fishingRecordRepository.findByFishSpeciesIdAndDeletedAtIsNull(fishSpecies.getId());

        return fishingRecords.stream().map(FishingRecordMarkerResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FishingRecordSummaryResponse getSummary(Long recordId) {
        FishingRecord fishingRecord = fishingRecordRepository.findByIdAndDeletedAtIsNull(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.FISHING_RECORD_NOT_FOUND));

        
        FishingRecordImage thumbnailImage = fishingRecordImageRepository.findByFishingRecordIdAndDisplayOrder(recordId, 0)
                .orElseThrow(() -> new AppException(ErrorCode.FISHING_RECORD_IMAGE_NOT_FOUND));

                String thumbnailUrl = pathResolver.resolveAccessUrl(StorageLocation.FISHING_RECORD_IMAGE, thumbnailImage.getStoredFilename());

        return FishingRecordSummaryResponse.from(fishingRecord, thumbnailUrl);
    }

    @Transactional(readOnly = true)
    public FishingRecordDetailResponse getDetail(Long recordId) {
        FishingRecord fishingRecord = fishingRecordRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.FISHING_RECORD_NOT_FOUND));

        List<FishingRecordImage> images = fishingRecordImageRepository.findByFishingRecordIdOrderByDisplayOrderAsc(recordId);

        List<String> imageUrls = images.stream()
                .map((image) -> pathResolver.resolveAccessUrl(StorageLocation.FISHING_RECORD_IMAGE, image.getStoredFilename()))
                .toList();

        return FishingRecordDetailResponse.from(fishingRecord, imageUrls);
    }
}
