package com.study.localmeet.config;

import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// Spring Security가 로그인 시 DB에서 유저 정보를 조회하는 서비스
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 회원이 없습니다: " + userEmail));

        return new User(
                users.getUserEmail(),
                users.getUserPassword(),
                List.of(new SimpleGrantedAuthority(users.getUserRole().getValue()))
        );
    }
}
