package com.moonlightharmony.fishingmapback.storage;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StoragePathResolver {
    
    private final FileStorageProperties properties;

    private String subPath(StorageLocation location) {
        return switch (location) {
            case FISH_SPECIES_IMAGE -> properties.getFishSpeciesImage();
            case FISHING_RECORD_IMAGE -> properties.getFishingRecordImage();
        };
    }

    public Path resolveStoragePath(StorageLocation location, String filename) {
        return Path.of(properties.getBasePath())
                .resolve(subPath((location)))
                .resolve(filename)
                .toAbsolutePath()
                .normalize();
    }

    public String resolveAccessUrl(StorageLocation location, String filename) {
        return properties.getBaseDomain()
                + properties.getBaseUrl()
                + "/" + subPath(location)
                + "/" + filename;
    }

    public Path resolveDirectory(StorageLocation location) {
        return Path.of(properties.getBasePath())
                .resolve(subPath(location))
                .toAbsolutePath()
                .normalize();
    }
}
