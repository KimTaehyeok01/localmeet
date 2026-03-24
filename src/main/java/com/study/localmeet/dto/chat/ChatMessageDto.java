package com.study.localmeet.dto.chat;

import com.study.localmeet.domain.chat.ChatMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageDto {

    private Long chatIdx;
    private Long meetingIdx;
    private Long userIdx;
    private String userNickname;
    private String chatContent;
    private LocalDateTime createdAt;

    // WebSocket으로 받을 때 (전송용)
    public ChatMessageDto(Long meetingIdx, String chatContent) {
        this.meetingIdx = meetingIdx;
        this.chatContent = chatContent;
    }

    // Entity -> DTO 변환
    public ChatMessageDto(ChatMessage entity) {
        this.chatIdx = entity.getChatIdx();
        this.meetingIdx = entity.getMeeting().getMeetingIdx();
        this.userIdx = entity.getUsers().getUserIdx();
        this.userNickname = entity.getUsers().getUserNickname();
        this.chatContent = entity.getChatContent();
        this.createdAt = entity.getCreatedAt();
    }
}
