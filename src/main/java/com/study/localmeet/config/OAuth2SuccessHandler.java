package com.study.localmeet.config;

import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = extractEmail(oAuth2User.getAttributes());

        Users users = usersRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("소셜 로그인 회원 정보를 찾을 수 없습니다."));

        String token = jwtUtil.createToken(
                users.getUserEmail(),
                Arrays.asList(users.getUserRole().getValue())
        );

        boolean needAddress = users.getUserAddress() == null || users.getUserAddress().isBlank();
        String redirectUrl = "/view/oauth2/success?token=" + token + (needAddress ? "&needAddress=true" : "");
        response.sendRedirect(redirectUrl);
    }

    @SuppressWarnings("unchecked")
    private String extractEmail(Map<String, Object> attributes) {
        if (attributes.containsKey("email")) {
            String email = (String) attributes.get("email");
            if (email != null && !email.isEmpty()) return email;
        }

        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                String email = (String) kakaoAccount.get("email");
                if (email != null && !email.isEmpty()) return email;
            }
        }

        if (attributes.containsKey("response")) {
            Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
            if (naverResponse != null) {
                String email = (String) naverResponse.get("email");
                if (email != null && !email.isEmpty()) return email;
            }
        }

        throw new RuntimeException("이메일 정보를 가져올 수 없습니다.");
    }
}
