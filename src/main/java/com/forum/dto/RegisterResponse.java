package com.forum.dto;

public record RegisterResponse(
        String message,
        Integer userId,
        String email,
        boolean emailVerificationRequired
) {
}
