package com.study.localmeet.domain.meetingmember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {

    // 모임별 참가자 목록 조회
    List<MeetingMember> findAllByMeeting_MeetingIdx(Long meetingIdx);

    // 모임별 전체 신청자 수 (승인 여부 무관)
    int countByMeeting_MeetingIdx(Long meetingIdx);

    // 모임별 승인된 참가자 수 조회
    int countByMeeting_MeetingIdxAndIsApproved(Long meetingIdx, Boolean isApproved);

    // 특정 유저가 특정 모임에 신청했는지 확인
    boolean existsByMeeting_MeetingIdxAndUsers_UserIdx(Long meetingIdx, Long userIdx);

    // 특정 유저의 특정 모임 참가 정보 조회
    Optional<MeetingMember> findByMeeting_MeetingIdxAndUsers_UserIdx(Long meetingIdx, Long userIdx);

    // 유저별 참가 모임 목록 조회
    List<MeetingMember> findAllByUsers_UserIdxAndIsApproved(Long userIdx, Boolean isApproved);
}
