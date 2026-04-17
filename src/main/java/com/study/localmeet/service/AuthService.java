package com.study.localmeet.service;

import com.study.localmeet.config.JwtUtil;
import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import com.study.localmeet.dto.auth.LoginRequestDto;
import com.study.localmeet.dto.auth.SignupRequestDto;
import com.study.localmeet.dto.auth.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public UserResponseDto signup(SignupRequestDto dto) {
        // 이메일 중복 체크
        if (usersRepository.existsByUserEmail(dto.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
        // 닉네임 중복 체크
        if (usersRepository.existsByUserNickname(dto.getUserNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        // 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(dto.getUserPassword());
        Users entity = usersRepository.save(dto.toEntity(encodedPassword));

        return new UserResponseDto(entity);
    }

    // 로그인 -> JWT 토큰 반환
    @Transactional(readOnly = true)
    public String login(LoginRequestDto dto) {
        Users users = usersRepository.findByUserEmail(dto.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(dto.getUserPassword(), users.getUserPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT 토큰 생성 후 반환 (Ex17JWT ApiController 패턴과 동일)
        return jwtUtil.createToken(
                users.getUserEmail(),
                Arrays.asList(users.getUserRole().getValue())
        );
    }

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(String userEmail) {
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return new UserResponseDto(users);
    }

    // 이메일 중복 체크
    @Transactional(readOnly = true)
    public boolean checkEmail(String userEmail) {
        return usersRepository.existsByUserEmail(userEmail);
    }

    // 닉네임 중복 체크
    @Transactional(readOnly = true)
    public boolean checkNickname(String userNickname) {
        return usersRepository.existsByUserNickname(userNickname);
    }

    // 주소 업데이트 (소셜 로그인 후)
    @Transactional
    public void updateAddress(String userEmail, String userAddress, Double userLat, Double userLng) {
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        users.update(users.getUserNickname(), userAddress, userLat, userLng);
    }
}
