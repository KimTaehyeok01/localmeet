package com.study.localmeet.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 내 알림 목록 (최신순, 최대 100개)
    List<Notification> findTop100ByReceiver_UserIdxOrderByCreatedAtDesc(Long userIdx);

    // 안 읽은 알림 수
    long countByReceiver_UserIdxAndIsReadFalse(Long userIdx);

    // 내 알림 전체 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true " +
           "WHERE n.receiver.userIdx = :userIdx AND n.isRead = false")
    void markAllReadByReceiver(@Param("userIdx") Long userIdx);

    // 회원 탈퇴 등 정리용
    void deleteAllByReceiver_UserIdx(Long userIdx);
}
