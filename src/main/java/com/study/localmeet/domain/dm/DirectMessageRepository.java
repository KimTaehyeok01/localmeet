package com.study.localmeet.domain.dm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    List<DirectMessage> findAllByConversation_ConvIdxOrderByCreatedAtAsc(Long convIdx);

    Optional<DirectMessage> findFirstByConversation_ConvIdxOrderByCreatedAtDesc(Long convIdx);

    // 특정 대화방의 미읽은 메시지 수
    @Query("SELECT COUNT(dm) FROM DirectMessage dm " +
           "JOIN ConversationMember cm ON cm.conversation.convIdx = dm.conversation.convIdx " +
           "WHERE dm.conversation.convIdx = :convIdx " +
           "AND cm.user.userIdx = :userIdx " +
           "AND dm.sender.userIdx != :userIdx " +
           "AND (cm.lastReadAt IS NULL OR dm.createdAt > cm.lastReadAt)")
    long countUnread(@Param("convIdx") Long convIdx, @Param("userIdx") Long userIdx);
}
