package com.moonlightharmony.fishingmapback.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.moonlightharmony.fishingmapback.exception.AppException;
import com.moonlightharmony.fishingmapback.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LocalFileStore implements FileStore {
    
    private final StoragePathResolver pathResolver;

    public List<String> storeFiles(List<MultipartFile> files, StorageLocation location) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        try {
            Files.createDirectories(pathResolver.resolveDirectory(location));
            return files.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> storeSingleFile(file, location))
                    .toList();
        } catch (IOException e) {
            throw new AppException((ErrorCode.FILE_STORE_FAILED));
        }
    }

    private String storeSingleFile(MultipartFile file, StorageLocation location) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);

        String storeFilename = UUID.randomUUID() + "." + extension;

        try {
            file.transferTo(pathResolver.resolveStoragePath(location, storeFilename));
            return storeFilename;
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_STORE_FAILED);
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new AppException(ErrorCode.FILE_STORE_FAILED);
        }

        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }


}
