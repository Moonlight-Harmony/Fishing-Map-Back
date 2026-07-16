package com.moonlightharmony.fishingmapback.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class LocalFileStoreTest {
    
    @TempDir
    Path tempDir;

    private LocalFileStore localFileStore;
    private StoragePathResolver pathResolver;

    @BeforeEach
    void setUp() {
        FileStorageProperties p = new FileStorageProperties();
        p.setBasePath(tempDir.toString());
        p.setBaseDomain("http://localhost:8080");
        p.setBaseUrl("/files");
        p.setFishSpeciesImage("image/fish_species");
        p.setFishingRecordImage("image/fishing_record");
        pathResolver = new StoragePathResolver(p);
        localFileStore = new LocalFileStore(pathResolver);
    }

    @Test
    void 파일_저장_성공() {
        MockMultipartFile file1 = createFile("test.jpg");
        MockMultipartFile file2 = createFile("test.jpg");

        List<String> result = localFileStore.storeFiles(
            List.of(file1, file2),
            StorageLocation.FISHING_RECORD_IMAGE
        );

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0)).endsWith(".jpg");

        Path storedPath = pathResolver.resolveStoragePath(StorageLocation.FISHING_RECORD_IMAGE, result.get(0));
        Assertions.assertThat(Files.exists(storedPath)).isTrue();
    }

    @Test
    void 파일_리스트가_null이면_빈리스트_반환() {
        List<String> result = localFileStore.storeFiles(null, StorageLocation.FISHING_RECORD_IMAGE);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void 파일_리스트가_비어있으면_빈리스트_반환() {
        List<String> result = localFileStore.storeFiles(List.of(), StorageLocation.FISHING_RECORD_IMAGE);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void 파일_삭제_성공() throws Exception {
        MockMultipartFile file = createFile("test.jpg");
        List<String> stored = localFileStore.storeFiles(
                List.of(file),
                StorageLocation.FISHING_RECORD_IMAGE
        );
        Path storedPath = pathResolver.resolveStoragePath(
                StorageLocation.FISHING_RECORD_IMAGE, stored.get(0));
        Assertions.assertThat(Files.exists(storedPath)).isTrue();

        localFileStore.deleteFile(stored.get(0), StorageLocation.FISHING_RECORD_IMAGE);

        Assertions.assertThat(Files.exists(storedPath)).isFalse();
    }

    @Test
    void 없는_파일_삭제는_예외없이_통과() {
        localFileStore.deleteFile("missing.jpg", StorageLocation.FISHING_RECORD_IMAGE);
    }

    private MockMultipartFile createFile(String filename) {
        return new MockMultipartFile("file", filename, "image/jpeg", "test".getBytes());
    }
}
