package com.study.localmeet.domain.dm;

import com.study.localmeet.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "direct_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dm_idx")
    private Long dmIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conv_idx", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_idx", nullable = false)
    private Users sender;

    @Column(name = "dm_content", nullable = false, columnDefinition = "TEXT")
    private String dmContent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public DirectMessage(Conversation conversation, Users sender, String dmContent) {
        this.conversation = conversation;
        this.sender = sender;
        this.dmContent = dmContent;
    }
}
