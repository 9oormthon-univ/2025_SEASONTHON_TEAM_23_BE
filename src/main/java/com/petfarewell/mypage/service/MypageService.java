package com.petfarewell.mypage.service;

import com.petfarewell.auth.dto.response.UserResponse;
import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.dailylog.repository.DailyLogRepository;
import com.petfarewell.letter.repository.LetterRepository;
import com.petfarewell.letter.repository.LetterTributeRepository;
import com.petfarewell.mypage.dto.UserActivitySummary;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final DailyLogRepository diaryRepository;
    private final LetterRepository letterRepository;
    private final LetterTributeRepository letterTributeRepository;

    @Transactional(readOnly = true)
    public UserActivitySummary getUserActivitySummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        long diaryCount = diaryRepository.countByUserId(user.getId());
        long letterCount = letterRepository.countByUserId(user.getId());
        long tributeCount = letterTributeRepository.countByUserId(user.getId());

        return new UserActivitySummary(diaryCount, letterCount, tributeCount);
    }

    @Transactional
    public void updateNickname(Long userId, String newNinkname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        user.updateNickname(newNinkname);
    }
}
