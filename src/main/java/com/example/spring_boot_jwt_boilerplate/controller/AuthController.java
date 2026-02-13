package com.example.spring_boot_jwt_boilerplate.controller;

import com.example.spring_boot_jwt_boilerplate.dto.auth.LoginRequest;
import com.example.spring_boot_jwt_boilerplate.dto.auth.LoginResponse;
import com.example.spring_boot_jwt_boilerplate.dto.auth.SignupRequest;
import com.example.spring_boot_jwt_boilerplate.dto.common.ApiResponse;
import com.example.spring_boot_jwt_boilerplate.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    /**
     * 로그인 엔드포인트
     */
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletResponse response) {

        LoginResponse loginResponse = authService.login(loginRequest, response);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @PostMapping("/signup")
    /**
     * 회원가입 엔드포인트
     */
    public ResponseEntity<ApiResponse<String>> signup(
            @RequestBody @Valid SignupRequest signupRequest) {

        String result = authService.signup(signupRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/check-email")
    /**
     * 중복이면 true, 없으면 false 반환
     */
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean isDuplicate = authService.isEmailDuplicate(email);
        return ResponseEntity.ok(ApiResponse.success(isDuplicate));
    }

    @GetMapping("/check-nickname")
    /**
     * 중복이면 true, 없으면 false 반환
     */
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        boolean isDuplicate = authService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(ApiResponse.success(isDuplicate));
    }

    @PostMapping("/reissue")
    /**
     * RT Alive 시 AT 반환
     */
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response) {

        LoginResponse loginResponse = authService.reissue(refreshToken, response);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @GetMapping("/test")
    /**
     * 인증 테스트 엔드포인트
     * Optional 원하면 삭제
     */
    public ResponseEntity<String> testMe(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok("인증 성공! 현재 사용자: " + email);
    }
}