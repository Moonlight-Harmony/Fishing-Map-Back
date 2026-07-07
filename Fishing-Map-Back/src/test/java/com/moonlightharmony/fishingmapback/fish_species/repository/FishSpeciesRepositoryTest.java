package com.moonlightharmony.fishingmapback.fish_species.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.moonlightharmony.fishingmapback.fish_species.entity.FishSpecies;

@DataJpaTest
class FishSpeciesRepositoryTest {
    
    @Autowired
    FishSpeciesRepository fishSpeciesRepository;

    @Test
    void 이름_일치하는_어종이_존재하면_true를_반환한다() {
        FishSpecies fishSpecies = FishSpecies.builder()
                .name("우럭")
                .scientificName("Sebastes schlegelii")
                .build();

        fishSpeciesRepository.save(fishSpecies);

        boolean exists = fishSpeciesRepository.existsByName("우럭");

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void 이름으로_어종을_찾을수있다() {
        FishSpecies fishSpecies = FishSpecies.builder()
                .name("우럭")
                .scientificName("Sebastes schlegelii")
                .build();

        fishSpeciesRepository.save(fishSpecies);

        Assertions.assertThat(fishSpeciesRepository.findByName("우럭")).isPresent();
        Assertions.assertThat(fishSpeciesRepository.findByName("우럭").get().getName()).isEqualTo("우럭");
    }
}
