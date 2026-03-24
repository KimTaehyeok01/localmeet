package com.study.localmeet.controller;

import com.study.localmeet.config.JwtUtil;
import com.study.localmeet.dto.chat.ChatMessageDto;
import com.study.localmeet.service.ChatService;
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

    // WebSocket 메시지 수신 및 브로드캐스트
    // 클라이언트가 /app/chat/send/{meetingIdx} 로 전송
    // -> /topic/meeting/{meetingIdx} 구독자들에게 브로드캐스트
    @MessageMapping("/chat/send/{meetingIdx}")
    public void sendMessage(@DestinationVariable Long meetingIdx,
                            @Payload ChatMessageDto dto,
                            org.springframework.messaging.simp.stomp.StompHeaderAccessor headerAccessor) {
        // WebSocket 헤더에서 JWT 토큰으로 인증
        String token = headerAccessor.getFirstNativeHeader("JWT_TOKEN");

        if (token != null && jwtUtil.validateToken(token)) {
            String userEmail = jwtUtil.getEmail(token);
            // DB에 저장 후 DTO 반환
            ChatMessageDto savedDto = chatService.save(meetingIdx, userEmail, dto.getChatContent());
            // 해당 모임 채팅방 구독자 전체에게 전송
            messagingTemplate.convertAndSend("/topic/meeting/" + meetingIdx, savedDto);
        }
    }

    // 채팅 내역 조회 REST API (입장 시 이전 메시지 불러오기)
    @GetMapping("/api/chat/{meetingIdx}")
    public List<ChatMessageDto> getChatHistory(@PathVariable Long meetingIdx) {
        return chatService.findAllByMeetingIdx(meetingIdx);
    }
}
