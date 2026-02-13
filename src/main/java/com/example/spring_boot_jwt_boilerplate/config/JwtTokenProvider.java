package com.example.spring_boot_jwt_boilerplate.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInMilliseconds;

    private Key key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Access Token 생성
     * @param email 사용자의 이메일을 받음
     * @return Access Token 반환
     */
    public String createAccessToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성
     * @return Refresh Token 반환
     */
    public String createRefreshToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자 이메일 추출
     * @param token 사용자의 Access Token
     * @return 이메일을 추출 (사용자 추출)
     */
    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰의 유효성 + 만료일자 확인
     * @param token 사용자의 Access Token
     * @return boolean(true/false)
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            // 만료 시간이 현재 시간보다 이전이면 false (만료됨)
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            // ★ 중요: 만료 예외는 밖으로 던집니다.
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            // 변조되거나 잘못된 형식의 토큰은 false 반환
            return false;
        }
    }

    /**
     * .env --> .yml --> variant
     * @return 설정된 RT 유효시간 반환
     */
    public long getRefreshTokenValidityInMilliseconds() {
        return this.refreshTokenValidityInMilliseconds;
    }
}