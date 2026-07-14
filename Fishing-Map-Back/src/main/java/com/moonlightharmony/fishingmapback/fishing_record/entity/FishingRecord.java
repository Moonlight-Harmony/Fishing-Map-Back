package com.moonlightharmony.fishingmapback.fishing_record.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.moonlightharmony.fishingmapback.entity.BaseTimeEntity;
import com.moonlightharmony.fishingmapback.fish_species.entity.FishSpecies;
import com.moonlightharmony.fishingmapback.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fishing_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FishingRecord extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fish_species_id", nullable = false)
    private FishSpecies fishSpecies;

    @Column(nullable = false)
    private LocalDateTime caughtAt;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(length = 30)
    private String region1DeptName;

    @Column(length = 30)
    private String region2DeptName;

    @Column(length = 30)
    private String region3DeptName;

    @Column(length = 1000)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Visibility visibility;

    @Column
    private LocalDateTime deletedAt;

    public enum Visibility {
        PUBLIC, PRIVATE
    }

    @Builder
    private FishingRecord(
            User user, 
            FishSpecies fishSpecies,
            BigDecimal latitude,
            BigDecimal longitude,
            String region1DeptName,
            String region2DeptName,
            String region3DeptName,
            String comment,
            Visibility visibility,
            LocalDateTime caughtAt
    ) {
        this.user = user;
        this.fishSpecies = fishSpecies;
        this.latitude = latitude;
        this.longitude = longitude;
        this.region1DeptName = region1DeptName;
        this.region2DeptName = region2DeptName;
        this.region3DeptName = region3DeptName;
        this.comment = comment;
        this.visibility = visibility;
        this.caughtAt = caughtAt;
    }
}
