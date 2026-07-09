package com.moonlightharmony.fishingmapback.storage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.moonlightharmony.fishingmapback.config.StorageConfig;

class FileStoragePropertiesBindingTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(StorageConfig.class);

    @Test
    void file_prefix_프로퍼티가_정상_바인딩된다() {
        contextRunner
                .withPropertyValues(
                        "file.base-domain=http://localhost:8080",
                        "file.base-path=./uploads",
                        "file.base-url=/files",
                        "file.fish-species-image=image/fish_species",
                        "file.fishing-record-image=image/fishing_record"
                )
                .run(context -> {
                    FIleStorageProperties properties = context.getBean(FIleStorageProperties.class);

                    Assertions.assertThat(properties.getBaseDomain()).isEqualTo("http://localhost:8080");
                    Assertions.assertThat(properties.getBasePath()).isEqualTo("./uploads");
                    Assertions.assertThat(properties.getBaseUrl()).isEqualTo("/files");
                    Assertions.assertThat(properties.getFishSpeciesImage()).isEqualTo("image/fish_species");
                    Assertions.assertThat(properties.getFishingRecordImage()).isEqualTo("image/fishing_record");
                });
    }
}