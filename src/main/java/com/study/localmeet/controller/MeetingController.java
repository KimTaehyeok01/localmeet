package com.study.localmeet.controller;

import com.study.localmeet.dto.meeting.MeetingResponseDto;
import com.study.localmeet.dto.meeting.MeetingSaveRequestDto;
import com.study.localmeet.service.MeetingService;
import com.study.localmeet.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final NotificationService notificationService;

    // 전체 모임 목록 조회
    @GetMapping
    public List<MeetingResponseDto> findAll() {
        return meetingService.findAll();
    }

    // 모임 단건 조회
    @GetMapping("/{meetingIdx}")
    public MeetingResponseDto findById(@PathVariable Long meetingIdx) {
        return meetingService.findById(meetingIdx);
    }

    // 모임 등록 (로그인 필요)
    @PostMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public String save(@ModelAttribute @Valid MeetingSaveRequestDto dto,
                       Authentication authentication) {
        try {
            Long newIdx = meetingService.save(dto, authentication.getName());
            return "<script>alert('모임 등록 성공'); location.href='/view/meetings';</script>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<script>alert('모임 등록 실패'); history.back();</script>";
        }
    }

    // 모임 수정 (작성자만 가능)
    @PostMapping("/{meetingIdx}/update")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public String update(@PathVariable Long meetingIdx,
                         @ModelAttribute @Valid MeetingSaveRequestDto dto,
                         Authentication authentication) {
        try {
            meetingService.update(meetingIdx, dto, authentication.getName());
            return "<script>alert('모임 수정 성공'); location.href='/view/meetings/" + meetingIdx + "';</script>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<script>alert('" + e.getMessage() + "'); history.back();</script>";
        }
    }

    // 모임 삭제 (작성자만 가능)
    @GetMapping("/{meetingIdx}/delete")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public String delete(@PathVariable Long meetingIdx,
                         Authentication authentication) {
        try {
            meetingService.delete(meetingIdx, authentication.getName());
            return "<script>alert('모임 삭제 성공'); location.href='/view/meetings';</script>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<script>alert('" + e.getMessage() + "'); location.href='/view/meetings';</script>";
        }
    }

    // 참가 신청 (로그인 필요)
    @PostMapping("/{meetingIdx}/join")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public String join(@PathVariable Long meetingIdx,
                       Authentication authentication) {
        try {
            String result = meetingService.join(meetingIdx, authentication.getName());

            // 모임 작성자에게 SSE 알림 전송
            MeetingResponseDto meetingDto = meetingService.findById(meetingIdx);
            notificationService.sendNotification(
                    meetingDto.getUserNickname(),
                    "[" + meetingDto.getMeetingTitle() + "] 새로운 참가 신청이 있습니다."
            );

            return result;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // 참가 승인 (모임 작성자만 가능)
    @PostMapping("/approve/{mmIdx}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public String approve(@PathVariable Long mmIdx,
                          Authentication authentication) {
        try {
            return meetingService.approve(mmIdx, authentication.getName());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // 주소 키워드 검색
    @GetMapping("/search")
    public List<MeetingResponseDto> search(@RequestParam String keyword) {
        return meetingService.searchByAddress(keyword);
    }
}
