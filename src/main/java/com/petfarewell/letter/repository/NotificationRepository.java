package com.petfarewell.letter.repository;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.Letter;
import com.petfarewell.letter.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByLetter(Letter letter);
    List<Notification> findAllByUser(User user);
}
