package com.study.localmeet.controller;

import com.study.localmeet.dto.notification.NotificationDto;
import com.study.localmeet.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // SSE 구독 (로그인한 유저가 알림 받기 위해 연결)
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public SseEmitter subscribe(Authentication authentication) {
        return notificationService.subscribe(authentication.getName());
    }

    // 내 알림 내역 목록
    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<NotificationDto> myNotifications(Authentication authentication) {
        return notificationService.getMyNotifications(authentication.getName());
    }

    // 안 읽은 알림 수
    @GetMapping("/unread-count")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public Map<String, Object> unreadCount(Authentication authentication) {
        return Map.of("count", notificationService.getUnreadCount(authentication.getName()));
    }

    // 전체 읽음 처리
    @PostMapping("/read-all")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, Object>> readAll(Authentication authentication) {
        notificationService.markAllRead(authentication.getName());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 알림 1건 읽음 처리
    @PostMapping("/{notiIdx}/read")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, Object>> readOne(@PathVariable Long notiIdx,
                                                       Authentication authentication) {
        notificationService.markRead(notiIdx, authentication.getName());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
