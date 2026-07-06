package com.moonlightharmony.fishingmapback.auth.dto;

import com.moonlightharmony.fishingmapback.user.dto.UserSummaryResponse;

public record LoginResponse(
    String accessToken,
    UserSummaryResponse user
) {}
