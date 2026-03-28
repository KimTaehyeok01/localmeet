package com.study.localmeet.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// SSE기반 실시간 알림 서비스
@Service
public class NotificationService {

    // 연결된 SSE 클라이언트 관리 (userEmail -> SseEmitter)
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 연결 (클라이언트가 /api/notifications/subscribe 요청)
    public SseEmitter subscribe(String userEmail) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 유지

        emitters.put(userEmail, emitter);

        // 연결 종료 시 emitter 제거
        emitter.onCompletion(() -> emitters.remove(userEmail));
        emitter.onTimeout(() -> emitters.remove(userEmail));
        emitter.onError(e -> emitters.remove(userEmail));

        // 연결 직후 초기 이벤트 전송 (연결 확인용)
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 성공"));
        } catch (IOException e) {
            emitters.remove(userEmail);
        }

        return emitter;
    }

    // 특정 유저에게 알림 전송
    public void sendNotification(String userEmail, String message) {
        SseEmitter emitter = emitters.get(userEmail);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(message));
            } catch (IOException e) {
                emitters.remove(userEmail);
            }
        }
    }
}
