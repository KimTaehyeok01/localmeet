package com.study.localmeet.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    // 이메일로 회원 조회 (로그인, 중복체크)
    Optional<Users> findByUserEmail(String userEmail);

    // 이메일 중복 체크
    boolean existsByUserEmail(String userEmail);

    // 닉네임 중복 체크
    boolean existsByUserNickname(String userNickname);
}
