package com.forum.service;

import com.forum.dto.NotificationResponse;
import com.forum.entity.Notification;
import com.forum.entity.NotificationType;
import com.forum.entity.User;
import com.forum.exception.ResourceNotFoundException;
import com.forum.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ConcurrentHashMap<Integer, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void create(User recipient, User actor, NotificationType type, String title, String body, String targetUrl) {
        if (recipient.getId().equals(actor.getId())) {
            return;
        }
        saveAndPublish(recipient, actor, type, title, body, targetUrl);
    }

    public void createSystem(User recipient, NotificationType type, String title, String body, String targetUrl) {
        saveAndPublish(recipient, null, type, title, body, targetUrl);
    }

    private void saveAndPublish(User recipient, User actor, NotificationType type, String title, String body, String targetUrl) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setActor(actor);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setTargetUrl(targetUrl);
        NotificationResponse response = toResponse(notificationRepository.save(notification));
        publishAfterCommit(recipient.getId(), response);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(Integer userId, int page, int size) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, PageRequest.of(page, Math.min(size, 100)))
                .map(this::toResponse);
    }

    public void markRead(Integer userId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndRecipientId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
    }

    public void markAllRead(Integer userId) {
        notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 1000))
                .forEach(notification -> notification.setRead(true));
    }

    @Transactional(readOnly = true)
    public long unreadCount(Integer userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Transactional(readOnly = true)
    public SseEmitter subscribe(Integer userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(userId, ignored -> ConcurrentHashMap.newKeySet()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        try {
            emitter.send(SseEmitter.event().name("connected").data("notifications"));
        } catch (IOException ex) {
            removeEmitter(userId, emitter);
        }
        return emitter;
    }

    private void publishAfterCommit(Integer recipientId, NotificationResponse response) {
        Runnable publish = () -> emitters.getOrDefault(recipientId, Set.of()).forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("notification").data(response));
            } catch (IOException ex) {
                removeEmitter(recipientId, emitter);
            }
        });
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish.run();
                }
            });
        } else {
            publish.run();
        }
    }

    private void removeEmitter(Integer userId, SseEmitter emitter) {
        Set<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) emitters.remove(userId, userEmitters);
        }
    }

    private NotificationResponse toResponse(Notification notification) {
        User actor = notification.getActor();
        return new NotificationResponse(notification.getId(), notification.getType(), notification.getTitle(),
                notification.getBody(), notification.getTargetUrl(), notification.isRead(), notification.getCreatedAt(),
                actor == null ? null : actor.getId(), actor == null ? null : actor.getDisplayName());
    }
}
