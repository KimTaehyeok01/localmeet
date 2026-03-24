package com.study.localmeet.dto.auth;

import com.study.localmeet.domain.user.Users;
import com.study.localmeet.enumeration.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String userEmail;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String userPassword;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다.")
    private String userNickname;

    private String userAddress;
    private Double userLat;
    private Double userLng;

    public Users toEntity(String encodedPassword) {
        return Users.builder()
                .userEmail(userEmail)
                .userPassword(encodedPassword)
                .userNickname(userNickname)
                .userRole(UserRole.ROLE_USER)
                .userAddress(userAddress)
                .userLat(userLat)
                .userLng(userLng)
                .build();
    }
}
