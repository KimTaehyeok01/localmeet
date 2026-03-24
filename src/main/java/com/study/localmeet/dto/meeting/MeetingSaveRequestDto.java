package com.study.localmeet.dto.meeting;

import com.study.localmeet.domain.meeting.Meeting;
import com.study.localmeet.domain.user.Users;
import com.study.localmeet.enumeration.MeetingStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeetingSaveRequestDto {

    @NotBlank(message = "모임 제목을 입력해주세요.")
    private String meetingTitle;

    @NotBlank(message = "모임 내용을 입력해주세요.")
    private String meetingContent;

    private String meetingAddress;
    private Double meetingLat;
    private Double meetingLng;

    @NotNull(message = "최대 인원을 입력해주세요.")
    @Min(value = 2, message = "최소 2명 이상이어야 합니다.")
    private Integer meetingMax;

    public Meeting toEntity(Users users) {
        return Meeting.builder()
                .meetingTitle(meetingTitle)
                .meetingContent(meetingContent)
                .meetingAddress(meetingAddress)
                .meetingLat(meetingLat)
                .meetingLng(meetingLng)
                .meetingMax(meetingMax)
                .meetingStatus(MeetingStatus.OPEN)
                .users(users)
                .build();
    }
}
