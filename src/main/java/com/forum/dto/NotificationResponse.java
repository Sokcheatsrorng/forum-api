package com.forum.dto;

import com.forum.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String title,
        String body,
        String targetUrl,
        boolean read,
        LocalDateTime createdAt,
        Integer actorId,
        String actorDisplayName
) {
}
