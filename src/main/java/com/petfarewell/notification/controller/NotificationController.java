package com.petfarewell.notification.controller;

import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.notification.dto.NotificationResponse;
import com.petfarewell.notification.entity.Notification;
import com.petfarewell.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "헌화 알림")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    @Operation(summary = "내 알림 목록 조회", description = "현재 로그인한 사용자의 읽지 않은 모든 알림을 최신순으로 조회합니다.")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<Notification> notifications = notificationService.findMyUnreadNotifications(userDetails.getId());

        List<NotificationResponse> response = notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
