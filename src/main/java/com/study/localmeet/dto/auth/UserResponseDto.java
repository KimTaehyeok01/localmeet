package com.study.localmeet.dto.auth;

import com.study.localmeet.domain.user.Users;
import com.study.localmeet.enumeration.UserRole;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto {

    private Long userIdx;
    private String userEmail;
    private String userNickname;
    private UserRole userRole;
    private String userAddress;
    private Double userLat;
    private Double userLng;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환 (기존 BoardResponseDto 패턴과 동일)
    public UserResponseDto(Users entity) {
        this.userIdx = entity.getUserIdx();
        this.userEmail = entity.getUserEmail();
        this.userNickname = entity.getUserNickname();
        this.userRole = entity.getUserRole();
        this.userAddress = entity.getUserAddress();
        this.userLat = entity.getUserLat();
        this.userLng = entity.getUserLng();
        this.createdAt = entity.getCreatedAt();
    }
}
