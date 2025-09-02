package com.petfarewell.letter.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TributeRequest {
    @NotNull(message = "메시지 Key는 필수입니다.")
    private String messageKey;
}
