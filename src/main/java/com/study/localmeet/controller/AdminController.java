package com.study.localmeet.controller;

import com.study.localmeet.dto.auth.UserResponseDto;
import com.study.localmeet.dto.meeting.MeetingResponseDto;
import com.study.localmeet.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AdminController {

    private final AdminService adminService;

    // 전체 회원 목록
    @GetMapping("/users")
    public List<UserResponseDto> findAllUsers() {
        return adminService.findAllUsers();
    }

    // 회원 강제 탈퇴
    @GetMapping("/users/{userIdx}/delete")
    public String deleteUser(@PathVariable Long userIdx) {
        try {
            adminService.deleteUser(userIdx);
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // 전체 모임 목록
    @GetMapping("/meetings")
    public List<MeetingResponseDto> findAllMeetings() {
        return adminService.findAllMeetings();
    }

    // 모임 강제 삭제
    @GetMapping("/meetings/{meetingIdx}/delete")
    public String deleteMeeting(@PathVariable Long meetingIdx) {
        try {
            adminService.deleteMeeting(meetingIdx);
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // 모임 상태 변경
    @PostMapping("/meetings/{meetingIdx}/status")
    public String updateStatus(@PathVariable Long meetingIdx, @RequestParam String status) {
        try {
            adminService.updateMeetingStatus(meetingIdx, status);
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
