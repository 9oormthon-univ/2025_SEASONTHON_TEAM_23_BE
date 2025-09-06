package com.petfarewell.star.service;

import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.dailylog.repository.DailyLogRepository;
import com.petfarewell.letter.repository.LetterRepository;
import com.petfarewell.letter.repository.LetterTributeRepository;
import com.petfarewell.star.dto.StarResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StarService {
    private final UserRepository userRepository;
    private final DailyLogRepository diaryRepository;
    private final LetterRepository letterRepository;
    private final LetterTributeRepository letterTributeRepository;

    public StarResponse getStar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        long diaryCount = diaryRepository.countByUserAndDeletedFalse(user);
        long letterCount = letterRepository.countByUser(user);
        long tributeCount = letterTributeRepository.getTotalTributeCountByUserId(user);

        return new StarResponse(diaryCount + letterCount + tributeCount);
    }
}

