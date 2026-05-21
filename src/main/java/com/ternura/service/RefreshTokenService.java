package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ternura.model.entity.RefreshToken;

// IService 是 MyBatis-Plus 提供的 Service 層介面，跟 ServiceImpl 是一對的
// 這裏跟實作類都繼承上的話，就能被其他人的 Service 調用該 Service 方法了
public interface RefreshTokenService extends IService<RefreshToken> {
    void deleteByToken(String token);
    void deleteByTokenAndUserId(String token, Long userId);
    void deleteExpiredTokensByUserId(Long userId);
    void findByToken(String token);
}
