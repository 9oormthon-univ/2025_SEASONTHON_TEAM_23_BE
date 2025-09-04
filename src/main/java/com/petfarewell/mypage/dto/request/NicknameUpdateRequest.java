package com.petfarewell.mypage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NicknameUpdateRequest {
    @NotBlank
    @Size(max = 20)
    private String nickname;
}
