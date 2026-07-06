package com.moonlightharmony.fishingmapback.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
    @NotBlank String email,
    @NotBlank @Size(min = 8, max= 20) String password,
    @NotBlank @Size(max = 50) String nickname
) {}
