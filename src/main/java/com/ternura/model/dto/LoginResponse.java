package com.ternura.model.dto;

import com.ternura.model.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private UserVO user;

    private String token;
    private String refreshToken;

    // Unix timestamp，毫秒
    private Long expiredAt;
    private Long refreshExpiredAt;
}
