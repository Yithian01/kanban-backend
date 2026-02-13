package com.example.spring_boot_jwt_boilerplate.dto.auth;

import com.example.spring_boot_jwt_boilerplate.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String nickname;
    private Role role;
}