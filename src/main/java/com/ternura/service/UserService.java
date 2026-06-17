package com.ternura.service;

import com.ternura.model.dto.*;

public interface UserService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String refreshToken);
    RefreshResponse refresh(String refreshToken);
}
