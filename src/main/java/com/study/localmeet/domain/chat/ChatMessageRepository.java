package com.study.localmeet.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 모임별 채팅 메시지 조회 (오래된 순)
    List<ChatMessage> findAllByMeeting_MeetingIdxOrderByCreatedAtAsc(Long meetingIdx);
}
