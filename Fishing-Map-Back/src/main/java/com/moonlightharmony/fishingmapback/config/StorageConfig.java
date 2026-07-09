package com.moonlightharmony.fishingmapback.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.moonlightharmony.fishingmapback.storage.FIleStorageProperties;

@Configuration
@EnableConfigurationProperties(FIleStorageProperties.class)
public class StorageConfig {
    
}
