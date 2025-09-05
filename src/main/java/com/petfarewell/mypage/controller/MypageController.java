package com.petfarewell.mypage.controller;

import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.mypage.dto.request.NicknameUpdateRequest;
import com.petfarewell.mypage.dto.response.UserActivitySummaryResponse;
import com.petfarewell.mypage.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Tag(name = "Mypage", description = "내 활동 요약")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/summary")
    @Operation(summary = "내 활동 요약 조회", description = "마이페이지에 표시될 활동 요약(일기 수, 편지 수, 헌화 수)을 조회")
    public ResponseEntity<UserActivitySummaryResponse> getMyActivitySummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserActivitySummaryResponse summary = mypageService.getUserActivitySummary(userDetails.getId());

        return ResponseEntity.ok(summary);
    }

    @PutMapping("/nickname")
    @Operation(summary = "내 닉네임 수정", description = "마이페이지에서 현재 로그인한 사용자의 닉네임을 수정")
    public ResponseEntity<Void> updateMyNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid NicknameUpdateRequest request) {

        mypageService.updateNickname(userDetails.getId(), request.getNickname());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/nickname")
    @Operation(summary = "로그인 직후 닉네임 받기", description = "로그인 직후 사용자로부터 닉네임 입력 받음")
    public ResponseEntity<Void> getNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid NicknameUpdateRequest request) {

        mypageService.updateNickname(userDetails.getId(), request.getNickname());
        return ResponseEntity.noContent().build();
    }
}
