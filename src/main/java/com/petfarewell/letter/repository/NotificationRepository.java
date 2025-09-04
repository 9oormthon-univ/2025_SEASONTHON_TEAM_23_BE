package com.petfarewell.letter.repository;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.Notification;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByUser(User user);
}
