package com.moonlightharmony.fishingmapback.fish_species.entity;

import com.moonlightharmony.fishingmapback.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fish_species")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FishSpecies extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 100)
    private String scientificName;

    @Builder
    private FishSpecies(String name, String scientificName) {
        this.name = name;
        this.scientificName = scientificName;
    }
    
}
