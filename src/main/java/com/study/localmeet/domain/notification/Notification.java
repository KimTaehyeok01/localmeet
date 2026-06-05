package com.study.localmeet.domain.notification;

import com.study.localmeet.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "notification")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_idx", nullable = false)
    private Long notiIdx;

    // 알림 수신자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users receiver;

    @Column(name = "noti_type", length = 20, nullable = false)
    private String notiType;  // MEETING / DM / FRIEND / GENERAL

    @Column(name = "noti_content", nullable = false, length = 255)
    private String content;

    // 클릭 시 이동할 링크 (선택)
    @Column(name = "noti_link", length = 255)
    private String link;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Notification(Users receiver, String notiType, String content, String link) {
        this.receiver = receiver;
        this.notiType = notiType != null ? notiType : "GENERAL";
        this.content = content;
        this.link = link;
        this.isRead = false;
    }

    public void markRead() {
        this.isRead = true;
    }
}
