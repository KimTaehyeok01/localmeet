package com.study.localmeet.controller;

import com.study.localmeet.dto.auth.UserResponseDto;
import com.study.localmeet.dto.meeting.MeetingResponseDto;
import com.study.localmeet.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserResponseDto> findAllUsers() {
        return adminService.findAllUsers();
    }

    @DeleteMapping("/users/{userIdx}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userIdx) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            adminService.deleteUser(userIdx);
            result.put("success", true);
            result.put("message", "탈퇴 처리되었습니다.");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/meetings")
    public List<MeetingResponseDto> findAllMeetings() {
        return adminService.findAllMeetings();
    }

    @DeleteMapping("/meetings/{meetingIdx}")
    public ResponseEntity<Map<String, Object>> deleteMeeting(@PathVariable Long meetingIdx) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            adminService.deleteMeeting(meetingIdx);
            result.put("success", true);
            result.put("message", "삭제되었습니다.");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/meetings/{meetingIdx}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long meetingIdx,
                                                            @RequestParam String status) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            adminService.updateMeetingStatus(meetingIdx, status);
            result.put("success", true);
            result.put("message", "상태가 변경되었습니다.");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
