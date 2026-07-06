package com.moonlightharmony.fishingmapback.user.dto;

import com.moonlightharmony.fishingmapback.user.entity.User;

public record UserSummaryResponse(
    Long id,
    String email,
    String nickname,
    User.Role role
) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
            user.getId(),
            user.getEmail(), 
            user.getNickname(),
            user.getRole()
        );
    }
}
