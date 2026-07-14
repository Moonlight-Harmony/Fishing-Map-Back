package com.moonlightharmony.fishingmapback.fishing_record.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.moonlightharmony.fishingmapback.fish_species.entity.FishSpecies;
import com.moonlightharmony.fishingmapback.fish_species.repository.FishSpeciesRepository;
import com.moonlightharmony.fishingmapback.fishing_record.entity.FishingRecord;
import com.moonlightharmony.fishingmapback.user.entity.User;
import com.moonlightharmony.fishingmapback.user.repository.UserRepository;

@DataJpaTest
class FishingRecordRepositoryTest {
    
    @Autowired
    FishingRecordRepository fishingRecordRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FishSpeciesRepository fishSpeciesRepository;

    @Test
    void 어종아이디로_낚시기록을_찾을수있다() {
        User user = userRepository.save(createUser());
        FishSpecies fishSpecies = fishSpeciesRepository.save(createFishSpecies("우럭"));
        FishSpecies otherFishSpecies = fishSpeciesRepository.save(createFishSpecies("감성돔"));

        FishingRecord fishingRecord = fishingRecordRepository.save(createFishingRecord(user, fishSpecies));
        fishingRecordRepository.save(createFishingRecord(user, otherFishSpecies));

        List<FishingRecord> fishingRecords = fishingRecordRepository.findByFishSpeciesIdAndDeletedAtIsNull(fishSpecies.getId());

        Assertions.assertThat(fishingRecords).hasSize(1);
        Assertions.assertThat(fishingRecords.get(0).getId()).isEqualTo(fishingRecord.getId());
        Assertions.assertThat(fishingRecords.get(0).getFishSpecies().getId()).isEqualTo(fishSpecies.getId());
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

    private FishingRecord createFishingRecord(User user, FishSpecies fishSpecies) {
        return FishingRecord.builder()
                .user(user)
                .fishSpecies(fishSpecies)
                .caughtAt(LocalDateTime.of(2026, 7, 7, 18, 30))
                .latitude(new BigDecimal("34.516517"))
                .longitude(new BigDecimal("127.244330"))
                .region1DeptName("전남광주")
                .region2DeptName("고흥군")
                .region3DeptName("풍양면")
                .comment("방파제 낚시")
                .visibility(FishingRecord.Visibility.PUBLIC)
                .build();
    }
}
