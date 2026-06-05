package com.study.localmeet.domain.friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // 두 유저 간 관계 조회 (방향 무관)
    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.requester.userIdx = :a AND f.receiver.userIdx = :b) OR " +
           "(f.requester.userIdx = :b AND f.receiver.userIdx = :a)")
    Optional<Friendship> findBetween(@Param("a") Long aIdx, @Param("b") Long bIdx);

    boolean existsByRequester_UserIdxAndReceiver_UserIdx(Long requesterIdx, Long receiverIdx);

    // 내 수락된 친구 목록 (방향 무관)
    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.requester.userIdx = :userIdx OR f.receiver.userIdx = :userIdx) " +
           "AND f.status = 'ACCEPTED'")
    java.util.List<Friendship> findAcceptedFriends(@Param("userIdx") Long userIdx);

    // 내가 받은 친구 요청(대기중) 목록 - 내가 receiver 이고 PENDING 인 것
    @Query("SELECT f FROM Friendship f WHERE f.receiver.userIdx = :userIdx " +
           "AND f.status = 'PENDING' ORDER BY f.createdAt DESC")
    java.util.List<Friendship> findReceivedPending(@Param("userIdx") Long userIdx);

    // 내 수락된 친구 수
    @Query("SELECT COUNT(f) FROM Friendship f WHERE " +
           "(f.requester.userIdx = :userIdx OR f.receiver.userIdx = :userIdx) " +
           "AND f.status = 'ACCEPTED'")
    long countAcceptedFriends(@Param("userIdx") Long userIdx);
}
