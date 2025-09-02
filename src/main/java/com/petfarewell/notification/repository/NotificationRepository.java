package com.petfarewell.notification.repository;

import com.petfarewell.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);
}
