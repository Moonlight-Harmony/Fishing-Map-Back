package com.moonlightharmony.fishingmapback.storage;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileStore {

    List<String> storeFiles(List<MultipartFile> multipartFiles, StorageLocation location);

    void deleteFile(String storedFilename, StorageLocation location);
}
