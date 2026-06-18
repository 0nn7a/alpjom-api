package com.ternura.service;

import com.ternura.model.dto.*;

public interface UserService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String refreshToken);
    RefreshResponse refresh(String refreshToken);

    void updateUser(Long userId, ProfileRequest request);
    void updatePassword(Long userId, PasswordRequest request);
}
