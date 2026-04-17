package com.study.localmeet.domain.chat;

import com.study.localmeet.domain.meeting.Meeting;
import com.study.localmeet.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "chat_message")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_idx", nullable = false)
    private Long chatIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_idx", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users users;

    @Column(name = "chat_content", nullable = false, columnDefinition = "TEXT")
    private String chatContent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public ChatMessage(Meeting meeting, Users users, String chatContent) {
        this.meeting = meeting;
        this.users = users;
        this.chatContent = chatContent;
    }
}
