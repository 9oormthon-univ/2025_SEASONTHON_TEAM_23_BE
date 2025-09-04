package com.petfarewell.mypage.controller;

import com.petfarewell.auth.dto.response.AuthTokensResponse;
import com.petfarewell.auth.dto.response.UserResponse;
import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.mypage.dto.UserActivitySummary;
import com.petfarewell.mypage.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Tag(name = "Mypage", description = "내 활동 요약")
public class MypageController {

    private final MypageService mypageService;
    private final UserRepository userRepository;

    @GetMapping("/summary")
    @Operation(summary = "내 활동 요약 조회", description = "마이페이지에 표시될 활동 요약(일기 수, 편지 수, 헌화 수)을 조회")
    public ResponseEntity<UserActivitySummary> getMyActivitySummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserActivitySummary summary = mypageService.getUserActivitySummary(userDetails.getId());

        return ResponseEntity.ok(summary);
    }

    @PutMapping("/profile")
    @Operation(summary = "내 닉네임 수정", description = "마이페이지에서 현재 로그인한 사용자의 닉네임을 수정")
    public ResponseEntity<Void> updateMyNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody String request) {

        mypageService.updateNickname(userDetails.getId(), request);

        return ResponseEntity.noContent().build();
    }
}
