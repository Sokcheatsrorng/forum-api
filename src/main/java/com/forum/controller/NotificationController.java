package com.forum.controller;

import com.forum.dto.ActionResponse;
import com.forum.dto.NotificationResponse;
import com.forum.dto.UnreadNotificationCountResponse;
import com.forum.security.CustomUserDetails;
import com.forum.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.getNotifications(user.getId(), Math.max(page, 0), Math.max(size, 1)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadNotificationCountResponse> unreadCount(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(new UnreadNotificationCountResponse(notificationService.unreadCount(user.getId())));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ActionResponse> markRead(@AuthenticationPrincipal CustomUserDetails user,
                                                    @PathVariable Long notificationId) {
        notificationService.markRead(user.getId(), notificationId);
        return ResponseEntity.ok(new ActionResponse("Notification marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ActionResponse> markAllRead(@AuthenticationPrincipal CustomUserDetails user) {
        notificationService.markAllRead(user.getId());
        return ResponseEntity.ok(new ActionResponse("All notifications marked as read"));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal CustomUserDetails user) {
        return notificationService.subscribe(user.getId());
    }
}
