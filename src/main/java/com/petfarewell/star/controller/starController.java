package com.petfarewell.star.controller;

import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.star.dto.StarResponse;
import com.petfarewell.star.service.StarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/star")
@Tag(name = "Star", description = "별 단계 기능")
public class starController {

    private final StarService starService;

    @GetMapping
    @Operation(summary = "별 집계 값 조회 API", description = "별 단계에 쓰일 일기, 편지, 헌화 수를 합한 값 제공")
    public ResponseEntity<StarResponse> getStar(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        StarResponse response = starService.getStar(userDetails.getId());

        return ResponseEntity.ok(response);
    }
}
