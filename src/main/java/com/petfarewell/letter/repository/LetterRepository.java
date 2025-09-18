package com.petfarewell.letter.repository;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    Letter save(Letter letter);
    List<Letter> findAllByIsPublicOrderByCreatedAtDesc(boolean isPublic);
    List<Letter> findAllByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
}
