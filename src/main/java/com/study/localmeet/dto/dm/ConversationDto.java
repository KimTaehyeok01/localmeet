package com.study.localmeet.dto.dm;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConversationDto {
    private Long convIdx;
    private Long partnerUserIdx;
    private String partnerNickname;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private long unreadCount;
}
