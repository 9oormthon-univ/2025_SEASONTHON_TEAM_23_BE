package com.petfarewell.auth.controller;

import java.util.*;

import com.petfarewell.auth.dto.ErrorResponse;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.petfarewell.auth.dto.request.KakaoAuthRequest;
import com.petfarewell.auth.dto.request.RefreshTokenRequest;
import com.petfarewell.auth.dto.response.AuthTokensResponse;
import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.security.JwtTokenProvider;
import com.petfarewell.auth.service.KakaoService;
import com.petfarewell.auth.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "카카오 로그인 및 JWT 인증 API")
@Slf4j
public class AuthController {

    private final KakaoService kakaoService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/kakao")
    @Transactional
    @Operation(summary = "카카오 로그인", description = "인가 코드 또는 카카오 액세스 토큰으로 로그인/회원가입을 수행하고 JWT를 발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = AuthTokensResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> kakaoLoginFromMobile(@RequestBody KakaoAuthRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Request body is required"));
        }

        String code = request.getCode();
        String kakaoAccessToken = request.getKakaoAccessToken();

        if ((code == null || code.isBlank()) && (kakaoAccessToken == null || kakaoAccessToken.isBlank())) {
            return ResponseEntity.badRequest().body(Map.of("message", "code 또는 kakaoAccessToken 중 하나는 필수입니다."));
        }

        try {
            if (kakaoAccessToken == null || kakaoAccessToken.isBlank()) {
                kakaoAccessToken = kakaoService.getAccessTokenFromKakao(code);
            }

            var userInfo = kakaoService.getUserInfo(kakaoAccessToken);

            User user = userService.registerOrUpdateKakaoUser(userInfo);

            Map<String, Object> claims = new HashMap<>();
            claims.put("nickname", user.getNickname());

            String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(user.getId()), claims);
            String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(user.getId()), claims);

            userService.updateRefreshToken(user, refreshToken);

            AuthTokensResponse.UserSummary summary = new AuthTokensResponse.UserSummary(
                    user.getId(), user.getNickname(), user.getProfileImageUrl());
            AuthTokensResponse response = new AuthTokensResponse(accessToken, refreshToken, summary);

            log.info("Successfully authenticated user with kakaoId: {}", user.getKakaoId());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Kakao authentication failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during kakao authentication: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보 조회", description = "JWT 토큰으로 현재 로그인한 사용자 정보를 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = AuthTokensResponse.UserSummary.class))),
            @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getCurrentUser() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("Current user: {}", authentication);
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "인증되지 않은 사용자입니다."));
            }

            String userId = authentication.getName();
            log.info("current user userId: {}", userId);

            if ("anonymousUser".equals(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "유효한 JWT 토큰이 필요합니다."));
            }

            try {
                Long.parseLong(userId);
            } catch (NumberFormatException e) {
                log.error("Invalid userId format: {}", userId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "유효하지 않은 사용자 ID 형식입니다."));
            }
            
            User user = userService.getUserById(Long.parseLong(userId));

            var summary = new AuthTokensResponse.UserSummary(user.getId(), user.getNickname(),
                    user.getProfileImageUrl());
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("Failed to get current user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "갱신 성공", content = @Content(schema = @Schema(example = "{\"accessToken\":\"newAccessToken\"}"))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "refreshToken is required"));
        }

        try {
            var claims = jwtTokenProvider.parseClaims(request.getRefreshToken());
            String userId = claims.getSubject();
            User user = userService.getUserById(Long.parseLong(userId));

            if (!userService.validateRefreshToken(user, request.getRefreshToken())) {
                throw new RuntimeException("Invalid refresh token");
            }

            Map<String, Object> newClaims = new HashMap<>(claims);
            newClaims.remove("exp");
            newClaims.remove("iat");

            String newAccessToken = jwtTokenProvider.createAccessToken(userId, newClaims);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃 시 리프레시 토큰을 무효화")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(example = "{\"message\":\"로그아웃되었습니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "refreshToken is required"));
        }

        try {
            var claims = jwtTokenProvider.parseClaims(request.getRefreshToken());
            log.info("claims: {}", claims);
            String userId = claims.getSubject();
            User user = userService.getUserById(Long.parseLong(userId));

            if (!userService.validateRefreshToken(user, request.getRefreshToken())) {
                throw new RuntimeException("Invalid refresh token");
            }

            userService.logout(user);

            log.info("User logged out successfully: userId={}", userId);
            return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다."));

        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }
}


