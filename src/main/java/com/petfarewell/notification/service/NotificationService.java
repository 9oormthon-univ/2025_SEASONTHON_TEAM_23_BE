package com.petfarewell.notification.service;

import com.petfarewell.notification.entity.Notification;
import com.petfarewell.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> findMyUnreadNotifications(Long userId) {
        return notificationRepository.findAllByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
}

