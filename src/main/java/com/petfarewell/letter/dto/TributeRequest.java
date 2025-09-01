package com.petfarewell.letter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TributeRequest {
    @NotNull(message = "메시지 Id는 필수입니다.")
    private Long messageId;
}
