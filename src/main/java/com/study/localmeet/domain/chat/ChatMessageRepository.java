package com.study.localmeet.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByMeeting_MeetingIdxOrderByCreatedAtAsc(Long meetingIdx);

    void deleteAllByUsers_UserIdx(Long userIdx);

    @Modifying
    @Query("DELETE FROM ChatMessage c WHERE c.meeting.meetingIdx = :meetingIdx")
    void deleteAllByMeeting_MeetingIdx(@Param("meetingIdx") Long meetingIdx);

    @Modifying
    @Query("DELETE FROM ChatMessage c WHERE c.meeting.meetingIdx IN :meetingIds")
    void deleteAllByMeetingIdxIn(@Param("meetingIds") List<Long> meetingIds);
}
