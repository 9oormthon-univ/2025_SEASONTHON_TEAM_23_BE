package com.petfarewell.letter.dto.response;

import com.petfarewell.letter.entity.Letter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LetterResponse {

    private final Long id;
    private final String content;
    private final String photoUrl;
    private final int tributeCount;
    private final LocalDateTime createdAt;
    private final Boolean isPublic;
    private final Long userId;
    private final String nickname;

    public static LetterResponse from(Letter letter) {
        return new LetterResponse(
                letter.getId(),
                letter.getContent(),
                letter.getPhotoUrl(),
                letter.getTributeCount(),
                letter.getCreatedAt(),
                letter.getIsPublic(),
                letter.getUser().getId(),
                letter.getUser().getNickname()
        );
    }

    private LetterResponse(Long letterId, String content, String photoUrl, int tributeCount,
                           LocalDateTime createdAt, Boolean isPublic, Long userId, String nickname) {
        this.id = letterId;
        this.content = content;
        this.photoUrl = photoUrl;
        this.tributeCount = tributeCount;
        this.createdAt = createdAt;
        this.isPublic = isPublic;
        this.userId = userId;
        this.nickname = nickname;
    }
}
