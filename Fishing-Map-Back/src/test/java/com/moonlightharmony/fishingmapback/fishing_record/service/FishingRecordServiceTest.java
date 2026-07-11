package com.moonlightharmony.fishingmapback.fishing_record.service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.moonlightharmony.fishingmapback.fish_species.entity.FishSpecies;
import com.moonlightharmony.fishingmapback.fish_species.repository.FishSpeciesRepository;
import com.moonlightharmony.fishingmapback.fishing_record.dto.CreateFishingRecordRequest;
import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;
import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecordImage;
import com.moonlightharmony.fishingmapback.fishing_record.repository.FishingRecordImageRepository;
import com.moonlightharmony.fishingmapback.fishing_record.repository.FishingRecordRepository;
import com.moonlightharmony.fishingmapback.storage.StorageLocation;
import com.moonlightharmony.fishingmapback.storage.StoragePathResolver;
import com.moonlightharmony.fishingmapback.user.entity.User;
import com.moonlightharmony.fishingmapback.user.repository.UserRepository;

@SpringBootTest
@Transactional
class FishingRecordServiceTest {

    @TempDir
    static Path tempDir;

    @Autowired
    FishingRecordService fishingRecordService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FishSpeciesRepository fishSpeciesRepository;

    @Autowired
    FishingRecordRepository fishingRecordRepository;

    @Autowired
    FishingRecordImageRepository fishingRecordImageRepository;

    @Autowired
    StoragePathResolver storagePathResolver;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> "jdbc:h2:mem:fishing_record_service;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("file.base-path", () -> tempDir.toString());
        registry.add("file.base-domain", () -> "http://localhost:8080");
        registry.add("file.base-url", () -> "/files");
        registry.add("file.fish-species-image", () -> "image/fish_species");
        registry.add("file.fishing-record-image", () -> "image/fishing_record");
        registry.add("jwt.secret",
                () -> "test-secret-key-that-is-long-enough-for-hs256-algorithm");
    }

    @Test
    @DisplayName("낚시기록_저장_성공")
    void save_fishing_record_success() {
        User user = userRepository.save(createUser());
        FishSpecies fishSpecies = fishSpeciesRepository.save(createFishSpecies("우럭"));
        MockMultipartFile image1 = createImage("catch1.jpg");
        MockMultipartFile image2 = createImage("catch2.jpg");

        CreateFishingRecordRequest request = new CreateFishingRecordRequest(
                fishSpecies.getId(),
                LocalDateTime.of(2026, 7, 7, 18, 30),
                new BigDecimal("34.516517"),
                new BigDecimal("127.244330"),
                "전남광주",
                "고흥군",
                "풍양면",
                "방파제 낚시",
                FishingRecord.Visibility.PUBLIC,
                List.of(image1, image2)
        );

        Long recordId = fishingRecordService.create(user.getId(), request);

        FishingRecord saved = fishingRecordRepository.findById(recordId).orElseThrow();
        Assertions.assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        Assertions.assertThat(saved.getFishSpecies().getId()).isEqualTo(fishSpecies.getId());
        Assertions.assertThat(saved.getCaughtAt()).isEqualTo(LocalDateTime.of(2026, 7, 7, 18, 30));
        Assertions.assertThat(saved.getLatitude()).isEqualByComparingTo("34.516517");
        Assertions.assertThat(saved.getLongitude()).isEqualByComparingTo("127.244330");
        Assertions.assertThat(saved.getRegion1DeptName()).isEqualTo("전남광주");
        Assertions.assertThat(saved.getRegion2DeptName()).isEqualTo("고흥군");
        Assertions.assertThat(saved.getRegion3DeptName()).isEqualTo("풍양면");
        Assertions.assertThat(saved.getComment()).isEqualTo("방파제 낚시");
        Assertions.assertThat(saved.getVisibility()).isEqualTo(FishingRecord.Visibility.PUBLIC);

        List<FishingRecordImage> images =
                fishingRecordImageRepository.findByFishingRecordIdOrderByDisplayOrderAsc(recordId);
        Assertions.assertThat(images).hasSize(2);
        Assertions.assertThat(images.get(0).getDisplayOrder()).isEqualTo(0);
        Assertions.assertThat(images.get(1).getDisplayOrder()).isEqualTo(1);
        Assertions.assertThat(images.get(0).getStoredFilename()).endsWith(".jpg");
        Assertions.assertThat(images.get(1).getStoredFilename()).endsWith(".jpg");

        Path storedPath1 = storagePathResolver.resolveStoragePath(
                StorageLocation.FISHING_RECORD_IMAGE, images.get(0).getStoredFilename());
        Path storedPath2 = storagePathResolver.resolveStoragePath(
                StorageLocation.FISHING_RECORD_IMAGE, images.get(1).getStoredFilename());
        Assertions.assertThat(Files.exists(storedPath1)).isTrue();
        Assertions.assertThat(Files.exists(storedPath2)).isTrue();
    }

    private User createUser() {
        return User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build();
    }

    private FishSpecies createFishSpecies(String name) {
        return FishSpecies.builder()
                .name(name)
                .scientificName("scname")
                .build();
    }

    private MockMultipartFile createImage(String filename) {
        return new MockMultipartFile("images", filename, "image/jpeg", "test".getBytes());
    }
}
