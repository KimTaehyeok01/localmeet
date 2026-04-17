package com.study.localmeet.domain.meeting;

import com.study.localmeet.enumeration.MeetingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findAllByMeetingStatusOrderByCreatedAtDesc(MeetingStatus meetingStatus);

    List<Meeting> findAllByOrderByCreatedAtDesc();

    List<Meeting> findAllByUsers_UserIdxOrderByCreatedAtDesc(Long userIdx);

    List<Meeting> findAllByMeetingAddressContainingOrderByCreatedAtDesc(String keyword);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Meeting m WHERE m.meetingIdx = :meetingIdx")
    Optional<Meeting> findByIdWithLock(@Param("meetingIdx") Long meetingIdx);

    @Modifying
    @Query("DELETE FROM Meeting m WHERE m.users.userIdx = :userIdx")
    void deleteAllByUsersUserIdx(@Param("userIdx") Long userIdx);
}
