package com.petfarewell.letter.dto.response;

import com.petfarewell.letter.entity.Notification;
import lombok.Getter;

@Getter
public class NotificationResponse {
    private final Long unreadTributeCount;

    private NotificationResponse(Long unreadTributeCount) {
        this.unreadTributeCount = unreadTributeCount;
    }

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getUnreadTributeCount()
        );
    }
}
