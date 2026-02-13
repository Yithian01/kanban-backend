package com.example.spring_boot_jwt_boilerplate.service;

import com.example.spring_boot_jwt_boilerplate.config.JwtTokenProvider;
import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import com.example.spring_boot_jwt_boilerplate.domain.member.Role;
import com.example.spring_boot_jwt_boilerplate.dto.auth.LoginRequest;
import com.example.spring_boot_jwt_boilerplate.dto.auth.LoginResponse;
import com.example.spring_boot_jwt_boilerplate.dto.auth.SignupRequest;
import com.example.spring_boot_jwt_boilerplate.exception.CustomException;
import com.example.spring_boot_jwt_boilerplate.exception.ErrorCode;
import com.example.spring_boot_jwt_boilerplate.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    /**
     * 1. 이메일 확인
     * 2. 비밀번호 일치 확인
     * 3. 토큰 발급
     * 4. Redis에 Refresh Token 저장 (Key: 이메일, Value: 토큰, 만료시간: 1시간)
     * 5. Refresh Token을 HttpOnly 쿠키에 담기
     */
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILURE);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        long ttl = jwtTokenProvider.getRefreshTokenValidityInMilliseconds();

        redisTemplate.opsForValue().set(
                "RT:" + member.getEmail(),
                refreshToken,
                ttl,
                TimeUnit.MILLISECONDS
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(ttl / 1000)
                .sameSite("Strict") // CSRF 공격 방지
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 6. Access Token 및 회원 정보 반환
        return LoginResponse.builder()
                .accessToken(accessToken)
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }

    @Transactional
    /**
     * 1. Refresh Token 자체의 유효성(만료일자, 변조여부) 검사
     * 2. Refresh Token에서 이메일 추출
     * 3. Redis에 저장된 Refresh Token 꺼내기
     * 4. Redis에 토큰이 없거나, 클라이언트가 보낸 토큰과 다르면 에러
     * 5. 새로운 Access Token 및 Refresh Token 생성
     * 6. Redis 업데이트 (기존 꺼 덮어쓰기)
     * 7. 새로운 Refresh Token을 쿠키에 담기
     */
    public LoginResponse reissue(String refreshToken, HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtTokenProvider.getEmail(refreshToken);

        String savedRefreshToken = redisTemplate.opsForValue().get("RT:" + email);

        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(email);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        long ttl = jwtTokenProvider.getRefreshTokenValidityInMilliseconds();
        redisTemplate.opsForValue().set("RT:" + email, newRefreshToken, ttl, TimeUnit.MILLISECONDS);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true) // 로컬 테스트 시 false
                .path("/")
                .maxAge(ttl / 1000)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Transactional
    /**
     * 회원 가입 로직
     */
    public String signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
        }

        if (memberRepository.existsByNickname(request.getNickname())) {
             throw new CustomException(ErrorCode.NICKNAME_DUPLICATION);
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
        return "회원가입 성공!!";
    }

    /**
     * 회원 가입 시 중복 확인 함수
     * @param email 이메일 중복확인
     * @return 확인 결과
     */
    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    /**
     * 회원 가입 시 닉네임 중복 확인 함수
     * @param nickname 닉네임 중복확인
     * @return 확인 결과
     */
    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }
}