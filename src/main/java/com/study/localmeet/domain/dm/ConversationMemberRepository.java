package com.study.localmeet.domain.dm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {

    List<ConversationMember> findAllByConversation_ConvIdx(Long convIdx);

    Optional<ConversationMember> findByConversation_ConvIdxAndUser_UserIdx(Long convIdx, Long userIdx);

    boolean existsByConversation_ConvIdxAndUser_UserIdx(Long convIdx, Long userIdx);

    // 내 전체 미읽은 메시지 수 (모든 대화방 합산)
    @Query("SELECT COUNT(dm) FROM DirectMessage dm " +
           "JOIN ConversationMember cm ON cm.conversation.convIdx = dm.conversation.convIdx " +
           "WHERE cm.user.userIdx = :userIdx " +
           "AND dm.sender.userIdx != :userIdx " +
           "AND (cm.lastReadAt IS NULL OR dm.createdAt > cm.lastReadAt)")
    long countTotalUnread(@Param("userIdx") Long userIdx);
}
