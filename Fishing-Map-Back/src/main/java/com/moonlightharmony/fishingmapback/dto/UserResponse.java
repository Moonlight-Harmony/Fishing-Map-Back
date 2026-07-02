package com.moonlightharmony.fishingmapback.dto;

public record UserResponse(
    Long id,
    String userId,
    String email,
    String username,
    String token
) {}
