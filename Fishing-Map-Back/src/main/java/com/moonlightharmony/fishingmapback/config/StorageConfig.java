package com.moonlightharmony.fishingmapback.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.moonlightharmony.fishingmapback.storage.FileStorageProperties;
import com.moonlightharmony.fishingmapback.storage.FileStore;
import com.moonlightharmony.fishingmapback.storage.LocalFileStore;
import com.moonlightharmony.fishingmapback.storage.StoragePathResolver;

@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class StorageConfig {

    @Bean
    public StoragePathResolver storagePathResolver(FileStorageProperties properties) {
        return new StoragePathResolver(properties);
    }
    
    @Bean
    public FileStore fileStore(StoragePathResolver pathResolver) {
        return new LocalFileStore(pathResolver);
    }
}
