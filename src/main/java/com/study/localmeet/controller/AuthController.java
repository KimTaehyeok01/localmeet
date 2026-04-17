package com.study.localmeet.controller;

import com.study.localmeet.dto.auth.LoginRequestDto;
import com.study.localmeet.dto.auth.SignupRequestDto;
import com.study.localmeet.dto.auth.UserResponseDto;
import com.study.localmeet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@ModelAttribute @Valid SignupRequestDto dto) {
        UserResponseDto result = authService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@ModelAttribute @Valid LoginRequestDto dto) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            String token = authService.login(dto);
            result.put("success", true);
            result.put("token", token);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    @GetMapping("/mypage")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public UserResponseDto mypage(Authentication authentication) {
        return authService.getMyInfo(authentication.getName());
    }

    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String userEmail) {
        return authService.checkEmail(userEmail);
    }

    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestParam String userNickname) {
        return authService.checkNickname(userNickname);
    }

    @PostMapping("/address")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, Object>> updateAddress(
            @RequestParam String userAddress,
            @RequestParam Double userLat,
            @RequestParam Double userLng,
            Authentication authentication) {
        Map<String, Object> result = new LinkedHashMap<>();
        authService.updateAddress(authentication.getName(), userAddress, userLat, userLng);
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
}
