package com.petfarewell.letter.repository;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    Letter save(Letter letter);
    List<Letter> findAllByIsPublicOrderByCreatedAtDesc(boolean isPublic);
    Optional<Letter> findByUser(User user);
    List<Letter> findAllByUser(User user);
    long countByUser(User user);
}
