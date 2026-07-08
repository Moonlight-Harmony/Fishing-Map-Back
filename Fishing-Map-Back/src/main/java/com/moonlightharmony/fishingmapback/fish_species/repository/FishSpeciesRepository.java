package com.moonlightharmony.fishingmapback.fish_species.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moonlightharmony.fishingmapback.fish_species.entity.FishSpecies;

public interface FishSpeciesRepository extends JpaRepository<FishSpecies, Long> {
    
    boolean existsByName(String name);

    Optional<FishSpecies> findByName(String name);
}
