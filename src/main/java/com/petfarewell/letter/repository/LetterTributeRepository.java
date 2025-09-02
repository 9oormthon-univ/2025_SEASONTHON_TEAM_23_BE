package com.petfarewell.letter.repository;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.Letter;
import com.petfarewell.letter.entity.LetterTribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface LetterTributeRepository extends JpaRepository<LetterTribute, Long> {
    boolean existsByUserAndLetter(User user, Letter letter);
    List<LetterTribute> findAllByLetterId(Long letterId);
    List<LetterTribute> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
