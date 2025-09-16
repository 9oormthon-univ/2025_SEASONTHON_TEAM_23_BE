package com.petfarewell.auth.service;

import com.petfarewell.auth.dto.request.KakaoAuthRequest;
import com.petfarewell.auth.dto.response.AuthTokensResponse;
import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.auth.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final KakaoService kakaoService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Transactional
    public AuthTokensResponse kakaoLogin(KakaoAuthRequest request) {
        if (request == null || ((request.getCode() == null || request.getCode().isBlank()) && (request.getKakaoAccessToken() == null || request.getKakaoAccessToken().isBlank()))) {
            throw new IllegalArgumentException("code 또는 kakaoAccessToken 중 하나는 필수입니다.");
        }

        String kakaoAccessToken = request.getKakaoAccessToken();
        if (kakaoAccessToken == null || kakaoAccessToken.isBlank()) {
            kakaoAccessToken = kakaoService.getAccessTokenFromKakao(request.getCode());
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

        log.info("Successfully authenticated user with kakaoId: {}", user.getKakaoId());
        return new AuthTokensResponse(accessToken, refreshToken, summary);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }

    @Transactional
    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("리프레시 토큰이 필요합니다.");
        }

        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        String userId = claims.getSubject();
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (!userService.validateRefreshToken(user, refreshToken)) {
            throw new SecurityException("유효하지 않은 리프레시 토큰입니다.");
        }

        Map<String, Object> newClaims = new HashMap<>(claims);
        newClaims.remove("exp");
        newClaims.remove("iat");

        return jwtTokenProvider.createAccessToken(userId, newClaims);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("리프레시 토큰이 필요합니다.");
        }

        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        String userId = claims.getSubject();
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (!userService.validateRefreshToken(user, refreshToken)) {
            throw new SecurityException("유효하지 않은 리프레시 토큰입니다.");
        }

        userService.logout(user);
        log.info("User logged out successfully: userId={}", userId);
    }
}
