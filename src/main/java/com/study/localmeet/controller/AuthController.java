package com.study.localmeet.controller;

import com.study.localmeet.dto.auth.LoginRequestDto;
import com.study.localmeet.dto.auth.SignupRequestDto;
import com.study.localmeet.dto.auth.UserResponseDto;
import com.study.localmeet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// 회원가입 / 로그인 REST API (Ex17JWT ApiController 패턴과 동일)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public UserResponseDto signup(@ModelAttribute @Valid SignupRequestDto dto) {
        return authService.signup(dto);
    }

    // 로그인 -> JWT 토큰 반환
    @PostMapping("/login")
    public String login(@ModelAttribute @Valid LoginRequestDto dto) {
        try {
            return authService.login(dto);
        } catch (Exception e) {
            return "로그인 실패: " + e.getMessage();
        }
    }

    // 내 정보 조회 (JWT 인증 필요)
    @GetMapping("/mypage")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public UserResponseDto mypage(Authentication authentication) {
        return authService.getMyInfo(authentication.getName());
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String userEmail) {
        return authService.checkEmail(userEmail);
    }

    // 닉네임 중복 체크
    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestParam String userNickname) {
        return authService.checkNickname(userNickname);
    }
}
