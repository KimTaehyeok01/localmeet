package com.study.localmeet.controller;

import com.study.localmeet.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final MeetingService meetingService;

    @Value("${kakao.map.api-key}")
    private String kakaoMapApiKey;

    // 루트 접속 시 모임 목록으로
    @GetMapping("/")
    public String root() {
        return "redirect:/view/meetings";
    }

    // /view/** 하위 매핑
    @GetMapping("/view/login")
    public String loginForm() { return "auth/loginForm"; }

    @GetMapping("/view/signup")
    public String signupForm() { return "auth/signupForm"; }

    @GetMapping("/view/mypage")
    public String mypage() { return "auth/mypage"; }

    @GetMapping("/view/meetings")
    public String meetingList(Model model) {
        model.addAttribute("list", meetingService.findAll());
        model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
        return "meeting/listForm";
    }

    @GetMapping("/view/meetings/write")
    public String meetingWriteForm(Model model) {
        model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
        return "meeting/writeForm";
    }

    @GetMapping("/view/meetings/{meetingIdx}")
    public String meetingContent(@PathVariable Long meetingIdx, Model model) {
        model.addAttribute("dto", meetingService.findById(meetingIdx));
        model.addAttribute("meetingIdx", meetingIdx);
        model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
        return "meeting/contentForm";
    }

    @GetMapping("/view/meetings/{meetingIdx}/edit")
    public String meetingEditForm(@PathVariable Long meetingIdx, Model model) {
        model.addAttribute("dto", meetingService.findById(meetingIdx));
        model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
        return "meeting/updateForm";
    }

    @GetMapping("/view/oauth2/success")
    public String oauth2Success(@RequestParam String token,
                                @RequestParam(required = false) Boolean needAddress,
                                Model model) {
        model.addAttribute("token", token);
        model.addAttribute("needAddress", Boolean.TRUE.equals(needAddress));
        model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
        return "auth/oauth2Success";
    }

    @GetMapping("/view/admin")
    @Secured("ROLE_ADMIN")
    public String adminPage() {
        return "admin/adminPage";
    }
}
