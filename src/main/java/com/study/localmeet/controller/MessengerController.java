package com.study.localmeet.controller;

import com.study.localmeet.config.JwtUtil;
import com.study.localmeet.dto.dm.ConversationDto;
import com.study.localmeet.dto.dm.DirectMessageDto;
import com.study.localmeet.service.MessengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MessengerController {

    private final MessengerService messengerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;
    private final com.study.localmeet.service.NotificationService notificationService;

    // 내 대화 목록
    @GetMapping("/api/messages/conversations")
    public ResponseEntity<List<ConversationDto>> getConversations(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(messengerService.getMyConversations(auth.getName()));
    }

    // DM 대화방 조회 또는 생성
    @PostMapping("/api/messages/conversations/dm/{targetUserIdx}")
    public ResponseEntity<Map<String, Object>> getOrCreateDM(@PathVariable Long targetUserIdx,
                                                              Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        try {
            Long convIdx = messengerService.getOrCreateDM(auth.getName(), targetUserIdx);
            return ResponseEntity.ok(Map.of("convIdx", convIdx));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 메시지 목록 조회 (읽음 처리 포함)
    @GetMapping("/api/messages/conversations/{convIdx}")
    public ResponseEntity<List<DirectMessageDto>> getMessages(@PathVariable Long convIdx,
                                                              Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        try {
            return ResponseEntity.ok(messengerService.getMessages(convIdx, auth.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    // HTTP 메시지 전송
    @PostMapping("/api/messages/conversations/{convIdx}/send")
    public ResponseEntity<?> sendMessage(@PathVariable Long convIdx,
                                         @RequestBody Map<String, String> body,
                                         Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        try {
            DirectMessageDto dto = messengerService.sendMessage(convIdx, auth.getName(), body.get("content"));
            messagingTemplate.convertAndSend("/topic/dm/" + convIdx, dto);
            // SSE 알림 → 수신자에게 전송
            messengerService.getRecipientEmails(convIdx, auth.getName())
                    .forEach(email -> notificationService.sendDmNotification(
                            email, dto.getSenderNickname(), dto.getDmContent(), convIdx));
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 전체 미읽은 메시지 수
    @GetMapping("/api/messages/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication auth) {
        if (auth == null) return ResponseEntity.ok(Map.of("count", 0L));
        long count = messengerService.getTotalUnread(auth.getName());
        return ResponseEntity.ok(Map.of("count", count));
    }

    // 유저 검색 (새 DM 시작용)
    @GetMapping("/api/users/search")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(@RequestParam String nickname,
                                                                  Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(messengerService.searchUsers(nickname, auth.getName()));
    }

    // WebSocket DM 전송
    @MessageMapping("/dm/send/{convIdx}")
    public void sendDMViaWebSocket(@DestinationVariable Long convIdx,
                                   @Payload Map<String, String> payload,
                                   StompHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("JWT_TOKEN");
        if (token == null || !jwtUtil.validateToken(token)) return;

        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) return;

        try {
            String senderEmail = jwtUtil.getEmail(token);
            DirectMessageDto dto = messengerService.sendMessage(convIdx, senderEmail, content);
            messagingTemplate.convertAndSend("/topic/dm/" + convIdx, dto);
            // SSE 알림
            messengerService.getRecipientEmails(convIdx, senderEmail)
                    .forEach(email -> notificationService.sendDmNotification(
                            email, dto.getSenderNickname(), dto.getDmContent(), convIdx));
        } catch (Exception ignored) {}
    }
}
