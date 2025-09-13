package com.petfarewell.letter.service;

import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.letter.dto.request.LetterRequest;
import com.petfarewell.letter.entity.Letter;
import com.petfarewell.letter.entity.Notification;
import com.petfarewell.letter.entity.upload.FileUploadService;
import com.petfarewell.letter.repository.LetterRepository;
import com.petfarewell.letter.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LetterService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final NotificationRepository notificationRepository;

    @Transactional
    public Letter saveLetter(Long userId, LetterRequest request, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = fileUploadService.upload(imageFile, "letter"); // S3 경로: /letter/
        }

        Letter newLetter = Letter.builder()
                .user(user)
                .content(request.getContent())
                .photoUrl(imageUrl) // 업로드된 URL or null
                .isPublic(request.getIsPublic())
                .tributedCount(0)
                .build();

        return letterRepository.save(newLetter);
    }

    @Transactional
    public List<Letter> findPublicLetters() {
        return letterRepository.findAllByIsPublicOrderByCreatedAtDesc(true);
    }

    @Transactional
    public List<Letter> findMyLetters(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        return letterRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Letter findLetter(Long letterId) {
        return letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("편지를 찾을 수 없습니다. ID: " + letterId));
    }

    @Transactional
    public Letter updateLetter(Long letterId, Long currentUserId, LetterRequest request, MultipartFile imageFile) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("편지를 찾을 수 없습니다. ID: " + letterId));

        if(!letter.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("편지를 수정할 권한이 없습니다.");
        }

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = fileUploadService.upload(imageFile, "letter"); // S3 경로: /letter/
        }

        letter.updateContent(request.getContent());
        letter.updatePhotoUrl(imageUrl);
        letter.updateIsPublic(request.getIsPublic());

        return letter;
    }

    @Transactional
    public void deleteLetter(Long letterId, Long currentUserId) {
        Letter letter = findLetter(letterId);

        if(!letter.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("편지를 삭제할 권한이 없습니다.");
        }

        letterRepository.delete(letter);
    }
}
