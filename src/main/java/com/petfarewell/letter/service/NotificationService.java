package com.petfarewell.letter.service;

import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.letter.dto.response.NotificationResponse;
import com.petfarewell.letter.entity.Letter;
import com.petfarewell.letter.entity.Notification;
import com.petfarewell.letter.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void saveNotification(User user, Letter letter) {
        notificationRepository.save(new Notification(user, letter));
    }

    @Transactional
    public List<NotificationResponse> findAndResetUnreadTributes(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        List<Notification> notifications = findNotifications(currentUser);

        List<NotificationResponse> response = notifications.stream()
                        .map(NotificationResponse::from)
                        .collect(Collectors.toList());

        notifications.forEach(Notification::resetTributeCount);

        return response;
    }

    @Transactional(readOnly = true)
    public List<Notification> findNotifications(User user) {
        return notificationRepository.findAllByUser(user);
    }
}