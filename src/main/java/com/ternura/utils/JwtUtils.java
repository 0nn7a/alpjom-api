package com.ternura.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component // 需要從 application.yml 注入變數
public class JwtUtils {
    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtUtils(
            @Value("${jwt.access-secret}") String accessSecret,
            @Value("${jwt.refresh-secret}") String refreshSecret
    ) {
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = Duration.ofHours(1).toMillis(); // 1hr
        this.refreshExpirationMs = Duration.ofDays(1).toMillis(); // 1day
    }

    // 產生 token：存 id、username、email
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(accessKey)
                .compact();
    }

    // 產生 refresh token：只存 id
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .claim("id", userId)
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(refreshKey)
                .compact();
    }

    // 解析 token
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 解析 refresh token
    public Claims parseRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(refreshKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 取得 token 到期時間戳（ms）
    public Long parseTokenTime(String token) {
        return parseToken(token).getExpiration().getTime();
    }

    // 取得 refresh token 到期時間戳（ms）
    public Long parseRefreshTokenTime(String token) {
        return parseRefreshToken(token).getExpiration().getTime();
    }

    // 取得 refresh token 到期時間
    public LocalDateTime parseRefreshTokenExpiration(String token) {
        Date expiration = parseRefreshToken(token).getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
