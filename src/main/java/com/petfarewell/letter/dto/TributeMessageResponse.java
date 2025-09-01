package com.petfarewell.letter.dto;

import com.petfarewell.letter.entity.TributeMessage;
import lombok.Getter;

@Getter
public class TributeMessageResponse {

    private final Long messageId;
    private final String content;

    private TributeMessageResponse(Long messageId, String content) {
        this.messageId = messageId;
        this.content = content;
    }

    public static TributeMessageResponse from(TributeMessage tributeMessage) {
        return new TributeMessageResponse(
                tributeMessage.getId(),
                tributeMessage.getTextKo()
        );
    }
}
