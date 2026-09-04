package com.ternura.security;

import org.jspecify.annotations.NonNull;
import tools.jackson.databind.ObjectMapper;
import com.ternura.model.common.Result;
import com.ternura.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;  // 用於整理並統一回應格式為自定義 Result 類

    // 實作 AuthenticationEntryPoint 並重寫 commence
    // 定義的是：若一個請求需要驗證，但驗證失敗或未驗證時，要怎麼處理
    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {
        // Filter 裡有記錄失敗原因（沒帶 Token / 格式錯誤 / 過期）就用它，沒有就走 ErrorCode 預設文案
        String message = (String) request.getAttribute(JwtAuthenticationFilter.AUTH_ERROR_ATTR);
        if (message == null) {
            message = ErrorCode.UNAUTHORIZED.getMessage();
        }

        Result<Void> result = Result.error(ErrorCode.UNAUTHORIZED.getCode(), message);
        response.setStatus(result.getCode());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result)); // 藉由 ObjectMapper 序列化
    }
}