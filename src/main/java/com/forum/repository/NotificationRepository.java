package com.forum.repository;

import com.forum.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Integer recipientId, Pageable pageable);

    Optional<Notification> findByIdAndRecipientId(Long id, Integer recipientId);

    long countByRecipientIdAndReadFalse(Integer recipientId);
}
