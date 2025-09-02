package com.petfarewell.letter.repository;

import com.petfarewell.letter.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    Letter save(Letter letter);
    List<Letter> findAllByIsPublicOrderByCreatedAtDesc(boolean isPublic);
    Optional<Letter> findById(Long id);
    List<Letter> findAllByUserId(Long Id);
}
