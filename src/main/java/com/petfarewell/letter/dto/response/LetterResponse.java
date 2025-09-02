package com.petfarewell.letter.dto.response;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.Letter;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class LetterResponse {

    private final Long id;
    private final String content;
    private final String photoUrl;
    private final int tributeCount;
    private final LocalDateTime createdAt;
    private final Boolean isPublic;
    private final Long userId;

    public static LetterResponse from(Letter letter) {
        return new LetterResponse(
                letter.getId(),
                letter.getContent(),
                letter.getPhotoUrl(),
                letter.getTributeCount(),
                letter.getCreatedAt(),
                letter.getIsPublic(),
                letter.getUser().getId()
        );
    }

    private LetterResponse(Long letterId, String content, String photoUrl, int tributeCount,
                           LocalDateTime createdAt, Boolean isPublic, Long userId) {
        this.id = letterId;
        this.content = content;
        this.photoUrl = photoUrl;
        this.tributeCount = tributeCount;
        this.createdAt = createdAt;
        this.isPublic = isPublic;
        this.userId = userId;
    }
}
