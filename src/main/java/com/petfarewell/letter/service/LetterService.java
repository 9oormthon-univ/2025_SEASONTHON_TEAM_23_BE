package com.petfarewell.letter.service;

import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.letter.dto.LetterRequest;
import com.petfarewell.letter.entity.Letter;
import com.petfarewell.letter.repository.LetterRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LetterService {

    private final LetterRepository letterRepository;
    private final UserRepository UserRepository;

    @Transactional
    public Letter saveLetter(Long userId, LetterRequest request) {

        User user = UserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Letter newLetter = Letter.builder()
                .content(request.getContent())
                .photoUrl(request.getPhotoUrl())
                .isPublic(request.getIsPublic())
                .user(user)
                .tributedCount(0)
                .build();

        return letterRepository.save(newLetter);
    }

    @Transactional
    public List<Letter> findPublicLetters() {

        return letterRepository.findAllByIsPublicOrderByCreatedAtDesc(true);
    }

    @Transactional
    public Letter findLetterById(Long id) {

        return letterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("편지를 찾을 수 없습니다. ID: " + id));
    }

    @Transactional
    public Letter updateLetter(Long letterId, Long currentUserId, LetterRequest request) {

        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("편지를 찾을 수 없습니다. ID: " + letterId));

        if(!letter.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("편지를 수정할 권한이 없습니다.");
        }

        letter.updateContent(request.getContent());
        letter.updatePhotoUrl(request.getPhotoUrl());
        letter.updateIsPublic(request.getIsPublic());

        return letter;
    }

    @Transactional
    public void deleteLetter(Long letterId, Long currentUserId) {

        Letter letter = findLetterById(letterId);

        if(!letter.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("편지를 삭제할 권한이 없습니다.");
        }

        letterRepository.delete(letter);
    }
}
