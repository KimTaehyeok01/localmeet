package com.study.localmeet.dto.meeting;

import com.study.localmeet.domain.meetingmember.MeetingMember;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetingMemberDto {

    private final Long mmIdx;
    private final String userNickname;
    private final boolean approved;
    private final LocalDateTime joinedAt;

    public MeetingMemberDto(MeetingMember mm) {
        this.mmIdx        = mm.getMmIdx();
        this.userNickname = mm.getUsers().getUserNickname();
        this.approved     = Boolean.TRUE.equals(mm.getIsApproved());
        this.joinedAt     = mm.getJoinedAt();
    }
}
