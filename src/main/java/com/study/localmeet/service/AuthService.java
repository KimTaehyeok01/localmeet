package com.study.localmeet.service;

import com.study.localmeet.config.JwtUtil;
import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import com.study.localmeet.dto.auth.LoginRequestDto;
import com.study.localmeet.dto.auth.SignupRequestDto;
import com.study.localmeet.dto.auth.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

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

        // JWT 토큰 생성 후 반환
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
        // 좌표가 전달되지 않으면(텍스트만 변경) 기존 좌표 유지
        Double lat = (userLat != null && userLat != 0.0) ? userLat : users.getUserLat();
        Double lng = (userLng != null && userLng != 0.0) ? userLng : users.getUserLng();
        users.update(users.getUserNickname(), userAddress, lat, lng);
    }

    // 닉네임 변경
    @Transactional
    public void updateNickname(String userEmail, String newNickname) {
        if (newNickname == null || newNickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }
        newNickname = newNickname.trim();
        if (newNickname.length() > 50) {
            throw new IllegalArgumentException("닉네임은 50자 이하여야 합니다.");
        }
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        // 본인 닉네임 그대로면 통과, 아니면 중복 체크
        if (!newNickname.equals(users.getUserNickname())
                && usersRepository.existsByUserNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
        users.updateNickname(newNickname);
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(String userEmail, String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 4) {
            throw new IllegalArgumentException("새 비밀번호는 4자 이상이어야 합니다.");
        }
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(currentPassword, users.getUserPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        users.updatePassword(passwordEncoder.encode(newPassword));
    }

    // 프로필 이미지 업로드
    @Transactional
    public String uploadProfileImage(String userEmail, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("파일을 선택해주세요.");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 5MB 이하여야 합니다.");
        }

        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 저장 디렉토리 생성
        Path profileDir = Paths.get(uploadDir, "profiles");
        Files.createDirectories(profileDir);

        // 기존 이미지 삭제
        if (users.getProfileImg() != null) {
            try { Files.deleteIfExists(Paths.get(uploadDir, users.getProfileImg().replaceFirst("^/uploads/", ""))); } catch (Exception ignored) {}
        }

        // 새 파일 저장
        String ext = contentType.contains("png") ? ".png" : contentType.contains("gif") ? ".gif" : ".jpg";
        String filename = "profiles/" + users.getUserIdx() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Files.copy(file.getInputStream(), Paths.get(uploadDir, filename));

        String imgUrl = "/uploads/" + filename;
        users.updateProfileImg(imgUrl);
        return imgUrl;
    }

    // 프로필 이미지 초기화 (기본 이모지로)
    @Transactional
    public void resetProfileImage(String userEmail) throws java.io.IOException {
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        if (users.getProfileImg() != null) {
            try {
                Files.deleteIfExists(
                        Paths.get(uploadDir, users.getProfileImg().replaceFirst("^/uploads/", ""))
                );
            } catch (Exception ignored) {}
        }
        users.updateProfileImg(null);
    }
}
