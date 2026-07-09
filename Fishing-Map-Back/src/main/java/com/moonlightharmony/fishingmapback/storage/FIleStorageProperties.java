package com.moonlightharmony.fishingmapback.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;


import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FIleStorageProperties {
    private String baseDomain;
    private String basePath;
    private String baseUrl;
    private String FishSpeciesImage;
    private String FishingRecordImage;
}
