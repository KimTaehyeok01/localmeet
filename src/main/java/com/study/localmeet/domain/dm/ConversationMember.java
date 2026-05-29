package com.study.localmeet.domain.dm;

import com.study.localmeet.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConversationMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cm_idx")
    private Long cmIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conv_idx", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users user;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Builder
    public ConversationMember(Conversation conversation, Users user) {
        this.conversation = conversation;
        this.user = user;
    }

    public void updateLastRead() {
        this.lastReadAt = LocalDateTime.now();
    }
}
