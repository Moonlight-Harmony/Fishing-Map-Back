package com.moonlightharmony.fishingmapback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FishingMapBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FishingMapBackApplication.class, args);
	}

}
