package com.petfarewell.letter.service;

import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.letter.dto.response.NotificationResponse;
import com.petfarewell.letter.entity.Notification;
import com.petfarewell.letter.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationResponse findAndResetUnreadTributes(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Notification notification = notificationRepository.findByUser(currentUser)
                .orElseGet(() -> notificationRepository.save(new Notification(currentUser)));

        NotificationResponse response = NotificationResponse.from(notification);

        notification.resetTributeCount();

        return response;
    }
}