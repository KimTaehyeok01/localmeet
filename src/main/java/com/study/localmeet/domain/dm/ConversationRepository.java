package com.study.localmeet.domain.dm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // 내가 참여한 모든 대화방 (최신 메시지 순)
    @Query("SELECT DISTINCT cm.conversation FROM ConversationMember cm " +
           "WHERE cm.user.userIdx = :userIdx")
    List<Conversation> findAllByUserIdx(@Param("userIdx") Long userIdx);

    // 두 유저 간 DM 대화방 조회 (1:1)
    @Query("SELECT cm1.conversation FROM ConversationMember cm1 " +
           "JOIN ConversationMember cm2 ON cm1.conversation = cm2.conversation " +
           "WHERE cm1.user.userIdx = :userId1 AND cm2.user.userIdx = :userId2")
    Optional<Conversation> findDMBetween(@Param("userId1") Long userId1,
                                          @Param("userId2") Long userId2);
}
