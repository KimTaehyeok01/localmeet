package com.study.localmeet.dto.meeting;

import com.study.localmeet.domain.meeting.Meeting;
import com.study.localmeet.enumeration.MeetingCategory;
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
    private MeetingCategory meetingCategory;
    private MeetingStatus meetingStatus;
    private LocalDateTime createdAt;

    // 작성자 정보
    private Long userIdx;
    private String userNickname;
    private String userEmail;

    // 현재 참가 인원
    private int currentCount;

    // 내 참가 상태 (NONE / PENDING / APPROVED) - 마이페이지 "참가한 모임"에서만 사용
    private String myStatus;

    // Entity -> DTO 변환
    public MeetingResponseDto(Meeting entity) {
        this.meetingIdx = entity.getMeetingIdx();
        this.meetingTitle = entity.getMeetingTitle();
        this.meetingContent = entity.getMeetingContent();
        this.meetingAddress = entity.getMeetingAddress();
        this.meetingLat = entity.getMeetingLat();
        this.meetingLng = entity.getMeetingLng();
        this.meetingMax = entity.getMeetingMax();
        this.meetingCategory = entity.getMeetingCategory();
        this.meetingStatus = entity.getMeetingStatus();
        this.createdAt = entity.getCreatedAt();
        this.userIdx = entity.getUsers() != null ? entity.getUsers().getUserIdx() : null;
        this.userNickname = entity.getUsers() != null ? entity.getUsers().getUserNickname() : "(탈퇴한 회원)";
        this.userEmail = entity.getUsers() != null ? entity.getUsers().getUserEmail() : null;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public void setMyStatus(String myStatus) {
        this.myStatus = myStatus;
    }
}
