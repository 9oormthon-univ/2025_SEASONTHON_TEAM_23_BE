package com.petfarewell.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.petfarewell.auth.dto.response.KakaoTokenResponse;
import com.petfarewell.auth.dto.response.KakaoUserInfoResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KakaoService {
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponse kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                // 4xx 응답일 때 상세 에러 로그 찍기
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("[Kakao Service] 4xx Error: {}", errorBody);
                            return Mono.error(new RuntimeException("Invalid Parameter: " + errorBody));
                        })
                )
                // 5xx 응답일 때 상세 에러 로그 찍기
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("[Kakao Service] 5xx Error: {}", errorBody);
                            return Mono.error(new RuntimeException("Internal Server Error: " + errorBody));
                        })
                )
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());

        return kakaoTokenResponseDto.getAccessToken();
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new RuntimeException("Access token is required");
        }

        KakaoUserInfoResponse userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("[Kakao Service] UserInfo 4xx Error: {}", errorBody);
                            return Mono.error(new RuntimeException("Invalid access token or insufficient permissions: " + errorBody));
                        })
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("[Kakao Service] UserInfo 5xx Error: {}", errorBody);
                            return Mono.error(new RuntimeException("Kakao service temporarily unavailable: " + errorBody));
                        })
                )
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();

        if (userInfo == null) {
            throw new RuntimeException("Failed to retrieve user info from Kakao");
        }

        if (userInfo.getId() == null) {
            throw new RuntimeException("Invalid user info: missing kakao ID");
        }

        log.info("[Kakao Service] Auth ID ---> {} ", userInfo.getId());

        // null 체크 강화
        if (userInfo.getKakaoAccount() != null && userInfo.getKakaoAccount().getProfile() != null) {
            log.info("[Kakao Service] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
            log.info("[Kakao Service] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
        } else {
            log.warn("[Kakao Service] Profile information is incomplete");
        }

        return userInfo;
    }
}


