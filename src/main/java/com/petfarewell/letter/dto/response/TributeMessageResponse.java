package com.petfarewell.letter.dto.response;

import com.petfarewell.letter.entity.TributeMessage;
import lombok.Getter;

@Getter
public class TributeMessageResponse {

    private final String key;
    private final String textKo;
    private final int sortOrder;

    private TributeMessageResponse(String messageKey, String content, int sortOrder) {
        this.key = messageKey;
        this.textKo = content;
        this.sortOrder = sortOrder;
    }

    public static TributeMessageResponse from(TributeMessage tributeMessage) {
        return new TributeMessageResponse(
                tributeMessage.getKey(),
                tributeMessage.getTextKo(),
                tributeMessage.getSortOrder()
        );
    }
}
