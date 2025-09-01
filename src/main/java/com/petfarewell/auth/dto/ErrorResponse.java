package com.petfarewell.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공통 에러 응답")
public class ErrorResponse {
    @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
    private String message;
}
