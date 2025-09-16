package com.petfarewell.auth.controller;

import com.petfarewell.auth.dto.request.KakaoAuthRequest;
import com.petfarewell.auth.dto.request.RefreshTokenRequest;
import com.petfarewell.auth.dto.response.AuthTokensResponse;
import com.petfarewell.auth.dto.response.MessageResponse;
import com.petfarewell.auth.dto.response.NewAccessTokenResponse;
import com.petfarewell.auth.dto.response.UserResponse;
import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "카카오 로그인 및 JWT 인증 API")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    @Operation(summary = "카카오 로그인", description = "인가 코드 또는 카카오 액세스 토큰으로 로그인/회원가입을 수행하고 JWT를 발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = AuthTokensResponse.class)))
    })
    public ResponseEntity<AuthTokensResponse> kakaoLoginFromMobile(@RequestBody KakaoAuthRequest request) {
        AuthTokensResponse response = authService.kakaoLogin(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보 조회", description = "JWT 토큰으로 현재 로그인한 사용자 정보를 조회")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = UserResponse.class)))
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = new UserResponse(
                userDetails.getId(),
                userDetails.getNickname(),
                userDetails.getProfileImageUrl()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제하고 모든 관련 데이터를 제거")
    @ApiResponse(responseCode = "204", description = "탈퇴 성공")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.deleteUser(userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급")
    @ApiResponse(responseCode = "200", description = "갱신 성공", content = @Content(schema = @Schema(implementation = NewAccessTokenResponse.class)))
    public ResponseEntity<NewAccessTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(new NewAccessTokenResponse(newAccessToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃 시 리프레시 토큰을 무효화")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    public ResponseEntity<MessageResponse> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(new MessageResponse("로그아웃되었습니다."));
    }
}