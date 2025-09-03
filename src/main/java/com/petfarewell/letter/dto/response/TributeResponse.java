package com.petfarewell.letter.dto.response;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.LetterTribute;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TributeResponse {

    private final Long id;
    private final Long letterId;
    private final Long fromUserId;
    private final String messageKey;
    private final LocalDateTime createdAt;

    private TributeResponse(Long tributeId, Long letterId, Long fromUserId,
                            String messagekey, LocalDateTime createdAt) {
        this.id = tributeId;
        this.letterId = letterId;
        this.fromUserId = fromUserId;
        this.messageKey = messagekey;
        this.createdAt = createdAt;
    }

    public static TributeResponse from(LetterTribute tribute) {
        return new TributeResponse(
                tribute.getId(),
                tribute.getLetter().getId(),
                tribute.getUser().getId(),
                tribute.getMessage().getKey(),
                tribute.getCreatedAt()
        );
    }
}