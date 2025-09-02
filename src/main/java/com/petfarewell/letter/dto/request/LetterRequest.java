package com.petfarewell.letter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LetterRequest {

    @NotBlank
    @Size(min = 1, max = 50)
    private String content;

    private String photoUrl;

    @NotNull(message = "공개 여부는 필수입니다.")
    private Boolean isPublic;
}
