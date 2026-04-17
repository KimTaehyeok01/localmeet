package com.study.localmeet.domain.user;

import com.study.localmeet.enumeration.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx", nullable = false)
    private Long userIdx;

    @Column(name = "user_email", nullable = false, unique = true, length = 100)
    private String userEmail;

    @Column(name = "user_password", nullable = false, length = 255)
    private String userPassword;

    @Column(name = "user_nickname", nullable = false, length = 50)
    private String userNickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRole userRole;

    @Column(name = "user_address", length = 255)
    private String userAddress;  // 동네 주소 (ex. 서울 마포구)

    @Column(name = "user_lat")
    private Double userLat;  // 위도

    @Column(name = "user_lng")
    private Double userLng;  // 경도

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Users(String userEmail, String userPassword, String userNickname,
                 UserRole userRole, String userAddress, Double userLat, Double userLng) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userNickname = userNickname;
        this.userRole = userRole;
        this.userAddress = userAddress;
        this.userLat = userLat;
        this.userLng = userLng;
    }

    // 프로필 수정
    public void update(String userNickname, String userAddress, Double userLat, Double userLng) {
        this.userNickname = userNickname;
        this.userAddress = userAddress;
        this.userLat = userLat;
        this.userLng = userLng;
    }
}
