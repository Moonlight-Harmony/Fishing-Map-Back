package com.moonlightharmony.fishingmapback.fishing_record.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.moonlightharmony.fishingmapback.exception.AppException;
import com.moonlightharmony.fishingmapback.exception.ErrorCode;
import com.moonlightharmony.fishingmapback.fish_species.entity.FishSpecies;
import com.moonlightharmony.fishingmapback.fish_species.repository.FishSpeciesRepository;
import com.moonlightharmony.fishingmapback.fishing_record.dto.CreateFishingRecordRequest;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordDetailResponse;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordImageResponse;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordMarkerResponse;
import com.moonlightharmony.fishingmapback.fishing_record.dto.FishingRecordSummaryResponse;
import com.moonlightharmony.fishingmapback.fishing_record.dto.UpdateFishingRecordRequest;
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
    public List<FishingRecordMarkerResponse> findMarkersByRegion(
            String region1DeptName,
            String region2DeptName,
            String region3DeptName
    ) {
        List<FishingRecord> records;

        if (region3DeptName != null) {
            records = fishingRecordRepository
                .findByRegion1DeptNameAndRegion2DeptNameAndRegion3DeptNameAndDeletedAtIsNull(
                        region1DeptName,
                        region2DeptName,
                        region3DeptName
            );
        } else if (region2DeptName != null) {
            records = fishingRecordRepository
                .findByRegion1DeptNameAndRegion2DeptNameAndDeletedAtIsNull(region1DeptName, region2DeptName);
        } else {
            records = fishingRecordRepository
                .findByRegion1DeptNameAndDeletedAtIsNull(region1DeptName);
        }

        return records.stream().map(FishingRecordMarkerResponse::from).toList();
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
        FishingRecord fishingRecord = fishingRecordRepository.findByIdAndDeletedAtIsNull(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.FISHING_RECORD_NOT_FOUND));

        List<FishingRecordImage> images = fishingRecordImageRepository.findByFishingRecordIdOrderByDisplayOrderAsc(recordId);

        List<FishingRecordImageResponse> imageResponses = images.stream()
                .map(image -> new FishingRecordImageResponse(
                        image.getId(),
                        pathResolver.resolveAccessUrl(
                                StorageLocation.FISHING_RECORD_IMAGE,
                                image.getStoredFilename())
                ))
                .toList();

        return FishingRecordDetailResponse.from(fishingRecord, imageResponses);
    }

    @Transactional
    public void delete(Long userId, Long recordId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FishingRecord fishingRecord = fishingRecordRepository.findByIdAndDeletedAtIsNull(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.FISHING_RECORD_NOT_FOUND));

        if (!fishingRecord.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        fishingRecord.softDelete();
    }

    @Transactional
    public void update(Long userId, Long recordId, UpdateFishingRecordRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FishingRecord fishingRecord = fishingRecordRepository.findByIdAndDeletedAtIsNull(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.FISHING_RECORD_NOT_FOUND));

        if (!fishingRecord.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        FishSpecies fishSpecies = fishSpeciesRepository.findById(request.fishSpeciesId())
                .orElseThrow(() -> new AppException(ErrorCode.FISH_SPECIES_NOT_FOUND));

        fishingRecord.update(
                fishSpecies,
                request.latitude(),
                request.longitude(),
                request.region1DeptName(),
                request.region2DeptName(),
                request.region3DeptName(),
                request.comment(),
                request.visibility(),
                request.caughtAt()
        );

        updateImages(fishingRecord, request.imageIds(), request.newImages());
    }

    private void updateImages(
            FishingRecord fishingRecord,
            List<String> imageIds,
            List<MultipartFile> newImages
    ) {
        List<String> orderedIds = imageIds == null ? List.of() : imageIds;
        List<MultipartFile> files = newImages == null ? List.of() : newImages.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

        List<Long> resolvedIds = orderedIds.stream()
                .map(this::parseImageId)
                .toList();

        long newSlotCount = resolvedIds.stream().filter(id -> id == null).count();
        if (newSlotCount != files.size()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        List<FishingRecordImage> existingImages =
                fishingRecordImageRepository.findByFishingRecordIdOrderByDisplayOrderAsc(fishingRecord.getId());
        Map<Long, FishingRecordImage> existingById = new HashMap<>();
        for (FishingRecordImage image : existingImages) {
            existingById.put(image.getId(), image);
        }

        Set<Long> keepIds = new HashSet<>();
        for (Long imageId : resolvedIds) {
            if (imageId == null) {
                continue;
            }
            if (!existingById.containsKey(imageId)) {
                throw new AppException(ErrorCode.FISHING_RECORD_IMAGE_NOT_FOUND);
            }
            if (!keepIds.add(imageId)) {
                throw new AppException(ErrorCode.BAD_REQUEST);
            }
        }

        List<FishingRecordImage> toDelete = existingImages.stream()
                .filter(image -> !keepIds.contains(image.getId()))
                .toList();

        for (FishingRecordImage image : toDelete) {
            fishingRecordImageRepository.delete(image);
            fileStore.deleteFile(image.getStoredFilename(), StorageLocation.FISHING_RECORD_IMAGE);
        }

        List<String> storedFileNames = fileStore.storeFiles(files, StorageLocation.FISHING_RECORD_IMAGE);
        int newFileIndex = 0;

        for (int i = 0; i < resolvedIds.size(); i++) {
            Long imageId = resolvedIds.get(i);
            if (imageId == null) {
                fishingRecordImageRepository.save(
                        FishingRecordImage.builder()
                                .fishingRecord(fishingRecord)
                                .storedFilename(storedFileNames.get(newFileIndex++))
                                .displayOrder(i)
                                .build()
                );
            } else {
                existingById.get(imageId).changeDisplayOrder(i);
            }
        }
    }

    private Long parseImageId(String rawId) {
        if (rawId == null || rawId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(rawId.trim());
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
    }
}
