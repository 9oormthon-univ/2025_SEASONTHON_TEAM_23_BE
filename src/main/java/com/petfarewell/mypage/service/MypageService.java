package com.petfarewell.mypage.service;

import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.dailylog.repository.DailyLogRepository;
import com.petfarewell.letter.repository.LetterRepository;
import com.petfarewell.letter.repository.LetterTributeRepository;
import com.petfarewell.mypage.dto.response.UserActivitySummaryResponse;
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
    public UserActivitySummaryResponse getUserActivitySummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        long diaryCount = diaryRepository.countByUserAndDeletedFalse(user);
        long letterCount = letterRepository.countByUser(user);
        long tributeCount = letterTributeRepository.getTotalTributeCountByUserId(user);

        return new UserActivitySummaryResponse(diaryCount, letterCount, tributeCount);
    }

    @Transactional
    public void updateNickname(Long userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        user.updateNickname(newNickname);
    }
}
