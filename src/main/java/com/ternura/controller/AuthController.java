package com.ternura.controller;

import com.ternura.model.dto.LoginRequest;
import com.ternura.model.dto.LoginResponse;
import com.ternura.model.dto.RefreshResponse;
import com.ternura.model.dto.RegisterRequest;
import com.ternura.model.common.Result;
import com.ternura.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // Lombok 提供的註解，用來自動產生 final Bean 物件的建構子（不用 @Autowired）
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request){
        userService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    // Authorization: Bearer eyJhbGci...        ← Access Token
    // X-Refresh-Token: eyJhbGci...             ← Refresh Token
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            userService.logout(refreshToken);
        }
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<RefreshResponse> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        RefreshResponse response = userService.refresh(refreshToken);
        return Result.success(response);
    }
}
