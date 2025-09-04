package com.petfarewell.mypage.controller;

import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.mypage.dto.UserActivitySummary;
import com.petfarewell.mypage.service.MypageService;
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
@RequestMapping("/mypage")
@Tag(name = "Mypage", description = "내 활동 요약")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/summary")
    @Operation(summary = "내 활동 요약 조회", description = "마이페이지에 표시될 활동 요약(일기 수, 편지 수, 헌화 수)을 조회")
    public ResponseEntity<UserActivitySummary> getMyActivitySummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserActivitySummary summary = mypageService.getUserActivitySummary(userDetails.getId());

        return ResponseEntity.ok(summary);
    }
}
