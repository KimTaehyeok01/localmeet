package com.study.localmeet.domain.meeting;

import com.study.localmeet.domain.user.Users;
import com.study.localmeet.enumeration.MeetingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "meeting")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_idx", nullable = false)
    private Long meetingIdx;

    @Column(name = "meeting_title", nullable = false, length = 100)
    private String meetingTitle;

    @Column(name = "meeting_content", nullable = false, columnDefinition = "TEXT")
    private String meetingContent;

    @Column(name = "meeting_address", length = 255)
    private String meetingAddress;

    @Column(name = "meeting_lat")
    private Double meetingLat;  // 위도

    @Column(name = "meeting_lng")
    private Double meetingLng;  // 경도

    @Column(name = "meeting_max", nullable = false)
    private Integer meetingMax;  // 최대 인원

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_status", nullable = false, length = 20)
    private MeetingStatus meetingStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 모임 작성자 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users users;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Meeting(String meetingTitle, String meetingContent, String meetingAddress,
                   Double meetingLat, Double meetingLng, Integer meetingMax,
                   MeetingStatus meetingStatus, Users users) {
        this.meetingTitle = meetingTitle;
        this.meetingContent = meetingContent;
        this.meetingAddress = meetingAddress;
        this.meetingLat = meetingLat;
        this.meetingLng = meetingLng;
        this.meetingMax = meetingMax;
        this.meetingStatus = meetingStatus != null ? meetingStatus : MeetingStatus.OPEN;
        this.users = users;
    }

    // 모임 수정
    public void update(String meetingTitle, String meetingContent, String meetingAddress,
                       Double meetingLat, Double meetingLng, Integer meetingMax) {
        this.meetingTitle = meetingTitle;
        this.meetingContent = meetingContent;
        this.meetingAddress = meetingAddress;
        this.meetingLat = meetingLat;
        this.meetingLng = meetingLng;
        this.meetingMax = meetingMax;
    }

    // 모임 상태 변경
    public void updateStatus(MeetingStatus meetingStatus) {
        this.meetingStatus = meetingStatus;
    }
}
