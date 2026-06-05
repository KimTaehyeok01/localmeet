package com.study.localmeet.controller;

import com.study.localmeet.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 친구 관계 상태 조회
    @GetMapping("/status/{targetUserIdx}")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable Long targetUserIdx,
                                                          Authentication auth) {
        if (auth == null) return ResponseEntity.ok(Map.of("status", "NONE"));
        return ResponseEntity.ok(friendService.getStatus(auth.getName(), targetUserIdx));
    }

    // 친구 요청 보내기
    @PostMapping("/request/{targetUserIdx}")
    public ResponseEntity<Map<String, Object>> sendRequest(@PathVariable Long targetUserIdx,
                                                            Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        try {
            String msg = friendService.sendRequest(auth.getName(), targetUserIdx);
            return ResponseEntity.ok(Map.of("success", true, "message", msg));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 내 친구 목록
    @GetMapping
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getMyFriends(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(friendService.getMyFriends(auth.getName()));
    }

    // 내가 받은 친구 요청(대기중) 목록
    @GetMapping("/received")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getReceived(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(friendService.getReceivedRequests(auth.getName()));
    }

    // 친구 요청 수락
    @PostMapping("/accept/{friendIdx}")
    public ResponseEntity<Map<String, Object>> acceptRequest(@PathVariable Long friendIdx,
                                                              Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        try {
            String msg = friendService.acceptRequest(auth.getName(), friendIdx);
            return ResponseEntity.ok(Map.of("success", true, "message", msg));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 친구 요청 거절
    @PostMapping("/reject/{friendIdx}")
    public ResponseEntity<Map<String, Object>> rejectRequest(@PathVariable Long friendIdx,
                                                             Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        try {
            String msg = friendService.rejectRequest(auth.getName(), friendIdx);
            return ResponseEntity.ok(Map.of("success", true, "message", msg));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
