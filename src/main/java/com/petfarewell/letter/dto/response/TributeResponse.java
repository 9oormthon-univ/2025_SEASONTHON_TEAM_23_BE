package com.petfarewell.letter.dto.response;

import com.petfarewell.letter.entity.LetterTribute;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TributeResponse {

    private final Long id;
    private final Long letterId;
    private final Long fromUserId;
    private final LocalDateTime createdAt;

    private TributeResponse(Long tributeId, Long letterId,
                            Long fromUserId, LocalDateTime createdAt) {
        this.id = tributeId;
        this.letterId = letterId;
        this.fromUserId = fromUserId;
        this.createdAt = createdAt;
    }

    public static TributeResponse from(LetterTribute tribute) {
        return new TributeResponse(
                tribute.getId(),
                tribute.getLetter().getId(),
                tribute.getUser().getId(),
                tribute.getCreatedAt()
        );
    }
}