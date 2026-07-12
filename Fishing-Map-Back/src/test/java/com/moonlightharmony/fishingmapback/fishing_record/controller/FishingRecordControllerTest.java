package com.moonlightharmony.fishingmapback.fishing_record.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.moonlightharmony.fishingmapback.fish_species.entity.FishSpecies;
import com.moonlightharmony.fishingmapback.fish_species.repository.FishSpeciesRepository;
import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;
import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecordImage;
import com.moonlightharmony.fishingmapback.fishing_record.repository.FishingRecordImageRepository;
import com.moonlightharmony.fishingmapback.fishing_record.repository.FishingRecordRepository;
import com.moonlightharmony.fishingmapback.storage.StorageLocation;
import com.moonlightharmony.fishingmapback.storage.StoragePathResolver;
import com.moonlightharmony.fishingmapback.user.entity.User;
import com.moonlightharmony.fishingmapback.user.repository.UserRepository;
import com.moonlightharmony.fishingmapback.util.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FishingRecordControllerTest {

    @TempDir
    static Path tempDir;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

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
                () -> "jdbc:h2:mem:fishing_record_controller;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
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
    @DisplayName("낚시기록_생성_성공")
    void create_success() throws Exception {
        User user = userRepository.save(createUser());
        FishSpecies fishSpecies = fishSpeciesRepository.save(createFishSpecies("우럭"));
        String token = jwtUtil.generateToken(String.valueOf(user.getId()));

        MockMultipartFile image = new MockMultipartFile(
                "images", "catch.jpg", "image/jpeg", "test".getBytes());

        mockMvc.perform(multipart("/api/v1/fishing_record")
                        .file(image)
                        .param("fishSpeciesId", String.valueOf(fishSpecies.getId()))
                        .param("caughtAt", "2026-07-07T18:30:00")
                        .param("latitude", "34.516517")
                        .param("longitude", "127.244330")
                        .param("region1DeptName", "전남광주")
                        .param("region2DeptName", "고흥군")
                        .param("region3DeptName", "풍양면")
                        .param("comment", "방파제 낚시")
                        .param("visibility", "PUBLIC")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @DisplayName("어종명으로_마커_조회_성공")
    void find_markers_success() throws Exception {
        User user = userRepository.save(createUser());
        FishSpecies fish1 = fishSpeciesRepository.save(createFishSpecies("우럭"));
        FishSpecies fish2 = fishSpeciesRepository.save(createFishSpecies("감성돔"));
        String token = jwtUtil.generateToken(String.valueOf(user.getId()));

        FishingRecord record1 = fishingRecordRepository.save(
                createFishingRecord(user, fish1, "34.516517", "127.244330"));
        FishingRecord record2 = fishingRecordRepository.save(
                createFishingRecord(user, fish1, "35.100000", "129.000000"));
        fishingRecordRepository.save(
                createFishingRecord(user, fish2, "33.500000", "126.500000"));

        mockMvc.perform(get("/api/v1/fishing_record/markers")
                        .param("fishSpeciesName", "우럭")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.id == " + record1.getId() + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == " + record2.getId() + ")]").exists());
    }

    @Test
    @DisplayName("낚시기록_요약_조회_성공")
    void get_summary_success() throws Exception {
        User user = userRepository.save(createUser());
        FishSpecies fish = fishSpeciesRepository.save(createFishSpecies("우럭"));
        String token = jwtUtil.generateToken(String.valueOf(user.getId()));

        FishingRecord record = fishingRecordRepository.save(
                createFishingRecord(user, fish, "34.516517", "127.244330"));
        FishingRecordImage thumbnail = fishingRecordImageRepository.save(
                FishingRecordImage.builder()
                        .fishingRecord(record)
                        .storedFilename("thumb.jpg")
                        .displayOrder(0)
                        .build()
        );

        String expectedUrl = storagePathResolver.resolveAccessUrl(
                StorageLocation.FISHING_RECORD_IMAGE, thumbnail.getStoredFilename());

        mockMvc.perform(get("/api/v1/fishing_record/{recordId}/summary", record.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(record.getId()))
                .andExpect(jsonPath("$.fishSpeciesName").value("우럭"))
                .andExpect(jsonPath("$.nickname").value("tester"))
                .andExpect(jsonPath("$.region1DeptName").value("전남광주"))
                .andExpect(jsonPath("$.thumbnailImageUrl").value(expectedUrl));
    }

    @Test
    @DisplayName("낚시기록_상세_조회_성공")
    void get_detail_success() throws Exception {
        User user = userRepository.save(createUser());
        FishSpecies fish = fishSpeciesRepository.save(createFishSpecies("우럭"));
        String token = jwtUtil.generateToken(String.valueOf(user.getId()));

        FishingRecord record = fishingRecordRepository.save(
                createFishingRecord(user, fish, "34.516517", "127.244330"));
        FishingRecordImage image1 = fishingRecordImageRepository.save(
                FishingRecordImage.builder()
                        .fishingRecord(record)
                        .storedFilename("first.jpg")
                        .displayOrder(0)
                        .build()
        );
        FishingRecordImage image2 = fishingRecordImageRepository.save(
                FishingRecordImage.builder()
                        .fishingRecord(record)
                        .storedFilename("second.jpg")
                        .displayOrder(1)
                        .build()
        );

        String url1 = storagePathResolver.resolveAccessUrl(
                StorageLocation.FISHING_RECORD_IMAGE, image1.getStoredFilename());
        String url2 = storagePathResolver.resolveAccessUrl(
                StorageLocation.FISHING_RECORD_IMAGE, image2.getStoredFilename());

        mockMvc.perform(get("/api/v1/fishing_record/{recordId}", record.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(record.getId()))
                .andExpect(jsonPath("$.fishSpeciesName").value("우럭"))
                .andExpect(jsonPath("$.comment").value("방파제 낚시"))
                .andExpect(jsonPath("$.visibility").value("PUBLIC"))
                .andExpect(jsonPath("$.nickname").value("tester"))
                .andExpect(jsonPath("$.imageUrls.length()").value(2))
                .andExpect(jsonPath("$.imageUrls[0]").value(url1))
                .andExpect(jsonPath("$.imageUrls[1]").value(url2));
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

    private FishingRecord createFishingRecord(
            User user, FishSpecies fishSpecies, String lat, String lng) {
        return FishingRecord.builder()
                .user(user)
                .fishSpecies(fishSpecies)
                .caughtAt(LocalDateTime.of(2026, 7, 7, 18, 30))
                .latitude(new BigDecimal(lat))
                .longitude(new BigDecimal(lng))
                .region1DeptName("전남광주")
                .region2DeptName("고흥군")
                .region3DeptName("풍양면")
                .comment("방파제 낚시")
                .visibility(FishingRecord.Visibility.PUBLIC)
                .build();
    }
}
