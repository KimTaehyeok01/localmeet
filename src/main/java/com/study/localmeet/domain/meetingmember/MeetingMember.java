package com.study.localmeet.domain.meetingmember;

import com.study.localmeet.domain.meeting.Meeting;
import com.study.localmeet.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "meeting_member")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MeetingMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mm_idx", nullable = false)
    private Long mmIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_idx", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users users;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;  // 승인 여부 (false=대기, true=승인)

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Builder
    public MeetingMember(Meeting meeting, Users users) {
        this.meeting = meeting;
        this.users = users;
        this.isApproved = false;
        this.joinedAt = LocalDateTime.now();
    }

    // 참가 승인
    public void approve() {
        this.isApproved = true;
    }
}
