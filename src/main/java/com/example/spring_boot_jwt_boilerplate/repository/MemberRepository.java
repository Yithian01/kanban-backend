package com.example.spring_boot_jwt_boilerplate.repository;

import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    /**
     * 로그인을 위해 이메일로 회원 정보를 조회하는 메서드
     * @param email 사용자가 전달한 이메일
     * @return Optional<Member>
     */
    Optional<Member> findByEmail(String email);

    /**
     * 회원가입 시 중복 가입 방지를 위한 체크 메서드
     * @param email 사용자가 전달한 이메일
     * @return boolean(true/false)
     */
    boolean existsByEmail(String email);

    /**
     * 회원가입 시 중복 가입 방지를 위한 체크 메서드
     * @param nickname 사용자가 전달한 닉네임
     * @return boolean(true/false)
     */
    boolean existsByNickname(String nickname);
}
