package com.petfarewell.notification.dto;

import com.petfarewell.notification.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Schema(description = "알림 응답 DTO")
public class NotificationResponse {

    @Schema(description = "알림 ID")
    private final Long notificationId;

    @Schema(description = "알림 내용")
    private final String content;

    @Schema(description = "알림 수신 시간 (한국 시간, yyyy-MM-dd HH:mm:ss)")
    private final String createdAt;

    @Schema(description = "읽음 여부")
    private final boolean isRead;

    private NotificationResponse(Long notificationId, String content, String createdAt, boolean isRead) {
        this.notificationId = notificationId;
        this.content = content;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getContent(),
                formatInstantToKST(notification.getCreatedAt()),
                notification.isRead()
        );
    }

    private static String formatInstantToKST(Instant instant) {
        if (instant == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Seoul"));
        return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
