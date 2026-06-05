package com.study.localmeet.service;

import com.study.localmeet.domain.notification.Notification;
import com.study.localmeet.domain.notification.NotificationRepository;
import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import com.study.localmeet.dto.notification.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    private final NotificationRepository notificationRepository;
    private final UsersRepository usersRepository;

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

    // 일반/모임 알림 (기존 호환 시그니처)
    public void sendNotification(String userEmail, String message) {
        sendNotification(userEmail, message, "MEETING", null);
    }

    // 알림 영속화 + SSE 푸시
    @Transactional
    public void sendNotification(String userEmail, String message, String type, String link) {
        persist(userEmail, type, message, link);

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

    // DM 수신 알림
    @Transactional
    public void sendDmNotification(String recipientEmail, String senderNickname,
                                    String content, Long convIdx) {
        String preview = content.length() > 40 ? content.substring(0, 40) + "…" : content;

        // 알림 내역 저장 (사람이 읽을 수 있는 형태)
        persist(recipientEmail, "DM",
                senderNickname + "님: " + preview, "/view/messages");

        SseEmitter emitter = emitters.get(recipientEmail);
        if (emitter == null) return;
        try {
            // 특수문자 이스케이프
            String safeNick    = senderNickname.replace("\\", "\\\\").replace("\"", "\\\"");
            String safeContent = preview.replace("\\", "\\\\").replace("\"", "\\\"");
            String data = "{\"senderNickname\":\"" + safeNick
                    + "\",\"content\":\"" + safeContent
                    + "\",\"convIdx\":" + convIdx + "}";
            emitter.send(SseEmitter.event().name("dm").data(data));
        } catch (IOException e) {
            emitters.remove(recipientEmail);
            log.warn("SSE DM notify failed for {}: {}", recipientEmail, e.getMessage());
        }
    }

    // ===== 알림 내역 (마이페이지) =====

    @Transactional(readOnly = true)
    public List<NotificationDto> getMyNotifications(String userEmail) {
        Users me = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return notificationRepository
                .findTop100ByReceiver_UserIdxOrderByCreatedAtDesc(me.getUserIdx())
                .stream().map(NotificationDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String userEmail) {
        return usersRepository.findByUserEmail(userEmail)
                .map(u -> notificationRepository.countByReceiver_UserIdxAndIsReadFalse(u.getUserIdx()))
                .orElse(0L);
    }

    @Transactional
    public void markAllRead(String userEmail) {
        usersRepository.findByUserEmail(userEmail)
                .ifPresent(u -> notificationRepository.markAllReadByReceiver(u.getUserIdx()));
    }

    @Transactional
    public void markRead(Long notiIdx, String userEmail) {
        notificationRepository.findById(notiIdx).ifPresent(n -> {
            if (n.getReceiver() != null
                    && n.getReceiver().getUserEmail().equals(userEmail)) {
                n.markRead();
            }
        });
    }

    // 알림 1건 저장 (수신자 없으면 조용히 무시)
    private void persist(String userEmail, String type, String content, String link) {
        try {
            usersRepository.findByUserEmail(userEmail).ifPresent(receiver ->
                    notificationRepository.save(Notification.builder()
                            .receiver(receiver)
                            .notiType(type)
                            .content(content)
                            .link(link)
                            .build()));
        } catch (Exception e) {
            log.warn("Notification persist failed for {}: {}", userEmail, e.getMessage());
        }
    }
}
