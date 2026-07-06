package com.ternura.model.dto;

import lombok.Data;

@Data
public class UserFollowResponse {
    private Long followerCount; // 粉絲總數
    private Long followingCount; // 追蹤中總數
    private boolean following; // 登入用戶是否已追蹤當前查看用戶
}
