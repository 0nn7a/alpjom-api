package com.ternura.model.dto;

import lombok.Data;

@Data
public class RefreshResponse {
    private String token;
    private Long expiredAt; // Unix timestamp，毫秒
}
