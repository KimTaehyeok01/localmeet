package com.study.localmeet.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 모임별 채팅 메시지 조회 (오래된 순)
    List<ChatMessage> findAllByMeeting_MeetingIdxOrderByCreatedAtAsc(Long meetingIdx);

    // 특정 유저의 채팅 메시지 삭제 (회원 탈퇴 시)
    void deleteAllByUsers_UserIdx(Long userIdx);
}
