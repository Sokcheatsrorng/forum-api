package com.forum.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Integer userId,
        String email,
        String displayName
) {
}
