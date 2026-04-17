package com.study.localmeet.controller;

import com.study.localmeet.dto.meeting.MeetingResponseDto;
import com.study.localmeet.dto.meeting.MeetingSaveRequestDto;
import com.study.localmeet.service.MeetingService;
import com.study.localmeet.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final NotificationService notificationService;

    @GetMapping
    public List<MeetingResponseDto> findAll() {
        return meetingService.findAll();
    }

    @GetMapping("/{meetingIdx}")
    public MeetingResponseDto findById(@PathVariable Long meetingIdx) {
        return meetingService.findById(meetingIdx);
    }

    @PostMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, Object>> save(@ModelAttribute @Valid MeetingSaveRequestDto dto,
                                                    Authentication authentication) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            Long newIdx = meetingService.save(dto, authentication.getName());
            result.put("success", true);
            result.put("message", "모임 등록 성공");
            result.put("meetingIdx", newIdx);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "모임 등록 실패");
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/{meetingIdx}/update")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long meetingIdx,
                                                      @ModelAttribute @Valid MeetingSaveRequestDto dto,
                                                      Authentication authentication) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            meetingService.update(meetingIdx, dto, authentication.getName());
            result.put("success", true);
            result.put("message", "모임 수정 성공");
            result.put("meetingIdx", meetingIdx);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/{meetingIdx}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long meetingIdx,
                                                      Authentication authentication) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            meetingService.delete(meetingIdx, authentication.getName());
            result.put("success", true);
            result.put("message", "모임 삭제 성공");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/{meetingIdx}/join")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, Object>> join(@PathVariable Long meetingIdx,
                                                    Authentication authentication) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            String message = meetingService.join(meetingIdx, authentication.getName());

            MeetingResponseDto meetingDto = meetingService.findById(meetingIdx);
            notificationService.sendNotification(
                    meetingDto.getUserEmail(),
                    "[" + meetingDto.getMeetingTitle() + "] 새로운 참가 신청이 있습니다."
            );

            result.put("success", true);
            result.put("message", message);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/approve/{mmIdx}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, Object>> approve(@PathVariable Long mmIdx,
                                                       Authentication authentication) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            String message = meetingService.approve(mmIdx, authentication.getName());
            result.put("success", true);
            result.put("message", message);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/search")
    public List<MeetingResponseDto> search(@RequestParam String keyword) {
        return meetingService.searchByAddress(keyword);
    }
}
