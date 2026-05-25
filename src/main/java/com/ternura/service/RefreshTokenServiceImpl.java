package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ternura.mapper.RefreshTokenMapper;
import com.ternura.model.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl extends ServiceImpl<RefreshTokenMapper, RefreshToken> implements RefreshTokenService {
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public void deleteByToken(String token) {
        refreshTokenMapper.deleteByToken(token);
    }

    @Override
    public void deleteByTokenAndUserId(String token, Long userId) {
        refreshTokenMapper.deleteByTokenAndUserId(token, userId);
    }

    @Override
    public void deleteExpiredTokensByUserId(Long userId) {
        refreshTokenMapper.deleteExpiredTokensByUserId(userId);
    }

    @Override
    public void selectByToken(String token) {
        refreshTokenMapper.selectByToken(token);
    }
}
