package com.forum.dto;

public record EmailVerificationResponse(
        String message,
        String email,
        boolean emailVerified
) {
}
