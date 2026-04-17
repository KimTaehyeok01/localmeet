package com.study.localmeet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userEmail) {
        SseEmitter oldEmitter = emitters.remove(userEmail);
        if (oldEmitter != null) {
            try {
                oldEmitter.complete();
            } catch (Exception ignored) {
            }
        }

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitters.put(userEmail, emitter);

        emitter.onCompletion(() -> emitters.remove(userEmail));
        emitter.onTimeout(() -> {
            emitters.remove(userEmail);
            emitter.complete();
        });
        emitter.onError(e -> {
            emitters.remove(userEmail);
            log.warn("SSE error for {}: {}", userEmail, e.getMessage());
        });

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 성공"));
        } catch (IOException e) {
            emitters.remove(userEmail);
        }

        return emitter;
    }

    public void sendNotification(String userEmail, String message) {
        SseEmitter emitter = emitters.get(userEmail);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                emitters.remove(userEmail);
                log.warn("SSE send failed for {}: {}", userEmail, e.getMessage());
            }
        }
    }
}
