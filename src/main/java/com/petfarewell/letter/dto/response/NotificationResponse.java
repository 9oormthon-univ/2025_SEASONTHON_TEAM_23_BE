package com.petfarewell.letter.dto.response;

import com.petfarewell.letter.entity.Notification;
import lombok.Getter;

@Getter
public class NotificationResponse {
    private final Long unreadTributeCount;
    private final Long letterId;
    private final String content;
    private final String photoUrl;

    private NotificationResponse(Long unreadTributeCount, Long letterId, String content, String photoUrl) {
        this.unreadTributeCount = unreadTributeCount;
        this.letterId = letterId;
        this.content = content;
        this.photoUrl = photoUrl;
    }

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getUnreadTributeCount(),
                notification.getLetter().getId(),
                notification.getLetter().getContent(),
                notification.getLetter().getPhotoUrl()
        );
    }
}
