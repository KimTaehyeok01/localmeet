package com.study.localmeet.controller;

import com.study.localmeet.config.JwtUtil;
import com.study.localmeet.dto.chat.ChatMessageDto;
import com.study.localmeet.service.ChatService;
import com.study.localmeet.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;
    private final SimpMessagingTemplate messagingTemplate;
    private final MeetingService meetingService;

    @MessageMapping("/chat/send/{meetingIdx}")
    public void sendMessage(@DestinationVariable Long meetingIdx,
                            @Payload ChatMessageDto dto,
                            org.springframework.messaging.simp.stomp.StompHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("JWT_TOKEN");

        if (token == null || !jwtUtil.validateToken(token)) {
            return;
        }

        String userEmail = jwtUtil.getEmail(token);

        if (!meetingService.canChat(meetingIdx, userEmail)) {
            return;
        }

        if (dto.getChatContent() == null || dto.getChatContent().trim().isEmpty()) {
            return;
        }

        ChatMessageDto savedDto = chatService.save(meetingIdx, userEmail, dto.getChatContent().trim());
        messagingTemplate.convertAndSend("/topic/meeting/" + meetingIdx, savedDto);
    }

    @GetMapping("/api/chat/{meetingIdx}")
    public List<ChatMessageDto> getChatHistory(@PathVariable Long meetingIdx) {
        return chatService.findAllByMeetingIdx(meetingIdx);
    }
}
