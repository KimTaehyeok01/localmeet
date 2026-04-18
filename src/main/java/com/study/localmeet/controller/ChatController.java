package com.study.localmeet.controller;

import com.study.localmeet.config.JwtUtil;
import com.study.localmeet.dto.chat.ChatMessageDto;
import com.study.localmeet.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send/{meetingIdx}")
    public void sendMessage(@DestinationVariable Long meetingIdx,
                            @Payload ChatMessageDto dto,
                            org.springframework.messaging.simp.stomp.StompHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("JWT_TOKEN");

        if (token == null || !jwtUtil.validateToken(token)) {
            return;
        }

        String userEmail = jwtUtil.getEmail(token);

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

    @PostMapping("/api/chat/{meetingIdx}")
    public ResponseEntity<?> sendChatByHttp(@PathVariable Long meetingIdx,
                                            @RequestBody ChatMessageDto dto,
                                            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }

        String chatContent = dto.getChatContent();
        if (chatContent == null || chatContent.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "메시지를 입력해주세요."));
        }

        String userEmail = authentication.getName();
        ChatMessageDto savedDto = chatService.save(meetingIdx, userEmail, chatContent.trim());
        messagingTemplate.convertAndSend("/topic/meeting/" + meetingIdx, savedDto);
        return ResponseEntity.ok(savedDto);
    }
}
