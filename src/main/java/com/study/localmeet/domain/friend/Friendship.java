package com.study.localmeet.domain.friend;

import com.study.localmeet.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendship",
       uniqueConstraints = @UniqueConstraint(columnNames = {"requester_idx", "receiver_idx"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_idx")
    private Long friendIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_idx", nullable = false)
    private Users requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_idx", nullable = false)
    private Users receiver;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // PENDING, ACCEPTED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Friendship(Users requester, Users receiver, String status) {
        this.requester = requester;
        this.receiver = receiver;
        this.status = status;
    }

    public void accept() {
        this.status = "ACCEPTED";
    }
}
