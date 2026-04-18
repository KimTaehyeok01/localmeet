package com.study.localmeet.dto.meeting;

import com.study.localmeet.domain.meeting.Meeting;
import com.study.localmeet.enumeration.MeetingStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetingResponseDto {

    private Long meetingIdx;
    private String meetingTitle;
    private String meetingContent;
    private String meetingAddress;
    private Double meetingLat;
    private Double meetingLng;
    private Integer meetingMax;
    private MeetingStatus meetingStatus;
    private LocalDateTime createdAt;

    // 작성자 정보
    private Long userIdx;
    private String userNickname;
    private String userEmail;

    // 현재 참가 인원
    private int currentCount;

    // Entity -> DTO 변환
    public MeetingResponseDto(Meeting entity) {
        this.meetingIdx = entity.getMeetingIdx();
        this.meetingTitle = entity.getMeetingTitle();
        this.meetingContent = entity.getMeetingContent();
        this.meetingAddress = entity.getMeetingAddress();
        this.meetingLat = entity.getMeetingLat();
        this.meetingLng = entity.getMeetingLng();
        this.meetingMax = entity.getMeetingMax();
        this.meetingStatus = entity.getMeetingStatus();
        this.createdAt = entity.getCreatedAt();
        this.userIdx = entity.getUsers() != null ? entity.getUsers().getUserIdx() : null;
        this.userNickname = entity.getUsers() != null ? entity.getUsers().getUserNickname() : "(탈퇴한 회원)";
        this.userEmail = entity.getUsers() != null ? entity.getUsers().getUserEmail() : null;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }
}
