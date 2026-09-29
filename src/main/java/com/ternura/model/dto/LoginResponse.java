package com.ternura.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private UserResponse user;

    private String token;
    private String refreshToken;

    // Unix timestamp，毫秒
    private Long expiredAt;
    private Long refreshExpiredAt;
}
