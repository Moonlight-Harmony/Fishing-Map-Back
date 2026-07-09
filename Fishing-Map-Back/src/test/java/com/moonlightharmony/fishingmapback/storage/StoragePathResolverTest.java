package com.moonlightharmony.fishingmapback.storage;

import java.nio.file.Path;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoragePathResolverTest {
    
    private StoragePathResolver pathResolver;

    @BeforeEach
    void setUp() {
        FIleStorageProperties p = new FIleStorageProperties();
        p.setBasePath("./uploads");
        p.setBaseDomain("http://localhost:8080");
        p.setBaseUrl("/files");
        p.setFishSpeciesImage("image/fish_species");
        p.setFishingRecordImage("image/fishing_record");
        pathResolver = new StoragePathResolver(p);
    }

    @Test
    void 저장_경로_정상_생성() {
        Path path = pathResolver.resolveStoragePath(StorageLocation.FISH_SPECIES_IMAGE, "test.jpg");
        Assertions.assertThat(path.toString()).contains("uploads").contains("image/fish_species").endsWith("test.jpg");
    }

    @Test
    void 접근_URL_정상_생성() {
        String url = pathResolver.resolveAccessUrl(StorageLocation.FISHING_RECORD_IMAGE, "test.png");
        Assertions.assertThat(url).isEqualTo("http://localhost:8080/files/image/fishing_record/test.png");
    }
}
