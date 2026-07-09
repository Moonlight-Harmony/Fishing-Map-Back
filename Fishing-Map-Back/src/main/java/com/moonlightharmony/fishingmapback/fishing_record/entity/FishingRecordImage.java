package com.moonlightharmony.fishingmapback.fishing_record.entity;

import com.moonlightharmony.fishingmapback.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "fishing_record_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FishingRecordImage extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fishing_record_id", nullable = false)
    private FishingRecord fishingRecord;

    @Column(nullable = false, length = 255)
    private String storedFilename;

    @Column(nullable = false)
    private int displayOrder;

    @Builder
    private FishingRecordImage(
            FishingRecord fishingRecord,
            String storedFilename,
            int displayOrder) {
        this.fishingRecord = fishingRecord;
        this.storedFilename = storedFilename;
        this.displayOrder = displayOrder;
    }
}
