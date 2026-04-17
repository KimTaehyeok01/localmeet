package com.study.localmeet.dto.auth;

import com.study.localmeet.domain.user.Users;
import com.study.localmeet.enumeration.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {

    private String email;
    private String nickname;
    private String provider;

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(attributes);
        }
        if ("naver".equals(registrationId)) {
            return ofNaver(attributes);
        }
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .email((String) response.get("email"))
                .nickname((String) response.get("nickname"))
                .provider("naver")
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile      = (Map<String, Object>) kakaoAccount.get("profile");
        return OAuthAttributes.builder()
                .email((String) kakaoAccount.get("email"))
                .nickname((String) profile.get("nickname"))
                .provider("kakao")
                .build();
    }

    public Users toEntity() {
        String uniqueNickname = provider.substring(0, 1).toUpperCase()
                + "_" + email.substring(0, Math.min(6, email.indexOf('@')));
        return Users.builder()
                .userEmail(email)
                .userPassword("OAUTH2_" + provider.toUpperCase())
                .userNickname(uniqueNickname)
                .userRole(UserRole.ROLE_USER)
                .build();
    }
}
