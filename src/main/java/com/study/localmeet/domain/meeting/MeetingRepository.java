package com.study.localmeet.domain.meeting;

import com.study.localmeet.enumeration.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // 상태별 모임 조회
    List<Meeting> findAllByMeetingStatusOrderByCreatedAtDesc(MeetingStatus meetingStatus);

    // 전체 모임 최신순 조회
    List<Meeting> findAllByOrderByCreatedAtDesc();

    // 작성자별 모임 조회
    List<Meeting> findAllByUsers_UserIdxOrderByCreatedAtDesc(Long userIdx);

    // 주소 키워드로 검색
    List<Meeting> findAllByMeetingAddressContainingOrderByCreatedAtDesc(String keyword);
}
