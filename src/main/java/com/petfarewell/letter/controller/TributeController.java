package com.petfarewell.letter.controller;

import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.letter.dto.response.NotificationResponse;
import com.petfarewell.letter.dto.response.TributeMessageResponse;
import com.petfarewell.letter.entity.TributeMessage;
import com.petfarewell.letter.service.NotificationService;
import com.petfarewell.letter.service.TributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tributes")
@Tag(name = "Tribute", description = "디지털 헌화")
public class TributeController {

    private final TributeService tributeService;
    private final NotificationService notificationService;

    @GetMapping("messages")
    @Operation(summary = "헌화 메시지 리스트 조회")
    public ResponseEntity<List<TributeMessageResponse>> getTributeMessages() {
        List<TributeMessage> messages = tributeService.findAllTributeMessage();

        List<TributeMessageResponse> response = messages.stream()
                .map(TributeMessageResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tributeId}")
    @Operation(summary = "헌화 취소")
    public ResponseEntity<Void> cancelTribute(
            @PathVariable Long tributeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        tributeService.cancelTribute(tributeId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notifications/recent")
    @Operation(summary = "헌화 알림", description = "프론트에서 특정 시간마다 이 API를 호출하여 그 시간까지 쌓인 헌화를 조회")
    public ResponseEntity<NotificationResponse> getRecentTributeNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        NotificationResponse response = notificationService.findAndResetUnreadTributes(userDetails.getId());

        return ResponseEntity.ok(response);
    }
}
