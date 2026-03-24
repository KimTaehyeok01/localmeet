package com.study.localmeet.controller;

import com.study.localmeet.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
}
