package com.study.localmeet.domain.meetingmember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {

    List<MeetingMember> findAllByMeeting_MeetingIdx(Long meetingIdx);

    int countByMeeting_MeetingIdx(Long meetingIdx);

    int countByMeeting_MeetingIdxAndIsApproved(Long meetingIdx, Boolean isApproved);

    boolean existsByMeeting_MeetingIdxAndUsers_UserIdx(Long meetingIdx, Long userIdx);

    Optional<MeetingMember> findByMeeting_MeetingIdxAndUsers_UserIdx(Long meetingIdx, Long userIdx);

    List<MeetingMember> findAllByUsers_UserIdxAndIsApproved(Long userIdx, Boolean isApproved);

    void deleteAllByUsers_UserIdx(Long userIdx);

    boolean existsByMeeting_MeetingIdxAndUsers_UserEmailAndIsApproved(
            Long meetingIdx, String userEmail, Boolean isApproved);

    boolean existsByMeeting_MeetingIdxAndUsers_UserEmail(Long meetingIdx, String userEmail);

    @Query("SELECT mm.meeting.meetingIdx, COUNT(mm) FROM MeetingMember mm GROUP BY mm.meeting.meetingIdx")
    List<Object[]> countAllGroupByMeeting();

    @Modifying
    @Query("DELETE FROM MeetingMember mm WHERE mm.meeting.meetingIdx = :meetingIdx")
    void deleteAllByMeeting_MeetingIdx(@Param("meetingIdx") Long meetingIdx);

    @Modifying
    @Query("DELETE FROM MeetingMember mm WHERE mm.meeting.meetingIdx IN :meetingIds")
    void deleteAllByMeetingIdxIn(@Param("meetingIds") List<Long> meetingIds);
}
