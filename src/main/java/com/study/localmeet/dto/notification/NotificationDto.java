package com.study.localmeet.dto.notification;

import com.study.localmeet.domain.notification.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationDto {

    private final Long notiIdx;
    private final String notiType;
    private final String content;
    private final String link;
    private final boolean read;
    private final LocalDateTime createdAt;

    public NotificationDto(Notification n) {
        this.notiIdx   = n.getNotiIdx();
        this.notiType  = n.getNotiType();
        this.content   = n.getContent();
        this.link      = n.getLink();
        this.read      = Boolean.TRUE.equals(n.getIsRead());
        this.createdAt = n.getCreatedAt();
    }
}
