package com.petfarewell.letter.service;

import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.letter.dto.request.TributeRequest;
import com.petfarewell.letter.entity.Letter;
import com.petfarewell.letter.entity.LetterTribute;
import com.petfarewell.letter.entity.TributeMessage;
import com.petfarewell.letter.repository.LetterRepository;
import com.petfarewell.letter.repository.LetterTributeRepository;
import com.petfarewell.letter.repository.TributeMessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TributeService {
    private final LetterTributeRepository letterTributeRepository;
    private final UserRepository userRepository;
    private final LetterRepository letterRepository;
    private final TributeMessageRepository tributeMessageRepository;

    @Transactional
    public LetterTribute addTribute(Long letterId, Long userId, TributeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("편지를 찾을 수 없습니다."));
        TributeMessage message = tributeMessageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new EntityNotFoundException("헌화 메시지를 찾을 수 없습니다."));

        if (letterTributeRepository.existsByUserAndLetter(user, letter)) {
            throw new IllegalArgumentException("이미 해당 편지에 헌화했습니다.");
        }

        LetterTribute tribute = LetterTribute.builder()
                .user(user)
                .letter(letter)
                .message(message)
                .build();

        letter.incrementTributeCount();

        return letterTributeRepository.save(tribute);
    }

    @Transactional(readOnly = true)
    public List<LetterTribute> findTributesByLetterId(Long letterId) {
        if (!letterRepository.existsById(letterId)) {
            throw new EntityNotFoundException("편지를 찾을 수 없습니다. ID: " + letterId);
        }

        return letterTributeRepository.findAllByLetterId(letterId);
    }

    @Transactional
    public void cancelTribute(Long tributeId, Long userId) {
        LetterTribute tribute = letterTributeRepository.findById(tributeId)
                .orElseThrow(() -> new EntityNotFoundException("헌화 정보를 찾을 수 없습니다."));

        if (!tribute.getUser().getId().equals(userId)) {
            throw new SecurityException("헌화를 취소할 권한이 없습니다.");
        }

        tribute.getLetter().decrementTributeCount();

        letterTributeRepository.delete(tribute);
    }

    @Transactional
    public List<TributeMessage> findAllTributeMessage() {
        return tributeMessageRepository.findAll();
    }
}
