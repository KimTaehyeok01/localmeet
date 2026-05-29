package com.study.localmeet.service;

import com.study.localmeet.domain.friend.Friendship;
import com.study.localmeet.domain.friend.FriendshipRepository;
import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final UsersRepository usersRepository;

    // 친구 요청 상태 조회 (NONE / PENDING_SENT / PENDING_RECEIVED / ACCEPTED)
    @Transactional(readOnly = true)
    public Map<String, Object> getStatus(String myEmail, Long targetUserIdx) {
        Users me = findUser(myEmail);
        Optional<Friendship> opt = friendshipRepository.findBetween(me.getUserIdx(), targetUserIdx);

        if (opt.isEmpty()) return Map.of("status", "NONE");

        Friendship f = opt.get();
        if ("ACCEPTED".equals(f.getStatus())) return Map.of("status", "ACCEPTED");

        // PENDING: 내가 보낸 건지 받은 건지
        boolean iSent = f.getRequester().getUserIdx().equals(me.getUserIdx());
        return Map.of("status", iSent ? "PENDING_SENT" : "PENDING_RECEIVED",
                      "friendIdx", f.getFriendIdx());
    }

    // 친구 요청 보내기
    @Transactional
    public String sendRequest(String myEmail, Long targetUserIdx) {
        Users me = findUser(myEmail);
        if (me.getUserIdx().equals(targetUserIdx)) {
            throw new IllegalArgumentException("자기 자신에게는 친구 요청을 할 수 없습니다.");
        }
        Users target = usersRepository.findById(targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Optional<Friendship> existing = friendshipRepository.findBetween(me.getUserIdx(), targetUserIdx);
        if (existing.isPresent()) {
            String status = existing.get().getStatus();
            if ("ACCEPTED".equals(status)) throw new IllegalArgumentException("이미 친구입니다.");
            throw new IllegalArgumentException("이미 친구 요청이 존재합니다.");
        }

        friendshipRepository.save(Friendship.builder()
                .requester(me).receiver(target).status("PENDING").build());
        return target.getUserNickname() + "님에게 친구 요청을 보냈습니다.";
    }

    // 친구 요청 수락
    @Transactional
    public String acceptRequest(String myEmail, Long friendIdx) {
        Friendship f = friendshipRepository.findById(friendIdx)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));
        Users me = findUser(myEmail);

        if (!f.getReceiver().getUserIdx().equals(me.getUserIdx())) {
            throw new IllegalArgumentException("수락 권한이 없습니다.");
        }
        f.accept();
        return f.getRequester().getUserNickname() + "님과 친구가 되었습니다.";
    }

    // 친구 목록 조회
    @Transactional(readOnly = true)
    public java.util.List<java.util.Map<String, Object>> getMyFriends(String myEmail) {
        Users me = findUser(myEmail);
        return friendshipRepository.findAcceptedFriends(me.getUserIdx()).stream()
                .map(f -> {
                    // 상대방이 누구인지 판별
                    com.study.localmeet.domain.user.Users partner =
                            f.getRequester().getUserIdx().equals(me.getUserIdx())
                                    ? f.getReceiver() : f.getRequester();
                    java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("userIdx",      partner.getUserIdx());
                    m.put("userNickname", partner.getUserNickname());
                    m.put("profileImg",   partner.getProfileImg() != null ? partner.getProfileImg() : "");
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    private Users findUser(String email) {
        return usersRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
    }
}
