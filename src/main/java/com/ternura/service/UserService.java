package com.ternura.service;

import com.ternura.model.dto.LoginRequest;
import com.ternura.model.dto.LoginResponse;
import com.ternura.model.dto.RegisterRequest;
import java.util.Map;

public interface UserService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String refreshToken);
}
