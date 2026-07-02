package com.moonlightharmony.fishingmapback.dto;

public record UserDetailResponse(
        Long id,
        String userId,
        String email,
        String username,
        String phoneNumber,
        String useYn,
        String language,
        String note
) {
}
