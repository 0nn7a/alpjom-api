package com.ternura.interceptor;

import com.ternura.model.common.Result;
import com.ternura.utils.CurrentHolder;
import com.ternura.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper; // 用於整理並統一回應格式為自定義 Result 類

    private void writeErrorRes(HttpServletResponse res, int code, String message) throws Exception {
        res.setStatus(code);
        res.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(code, message);
        res.getWriter().write(objectMapper.writeValueAsString(result)); // 藉由 ObjectMapper 序列化
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest req,
                             @NonNull HttpServletResponse res,
                             @NonNull Object handler) throws Exception {
        // 1. 獲取請求路徑
        String requestURI = req.getRequestURI();
        log.info("進入攔截器，當前請求路徑: {}", requestURI);

        // 2. 若為登入請求則直接放行
        // 註冊時已處理為不攔截登入、註冊、登出路徑 ⏳

        // 3. 獲取請求頭中的 Token
        String token = req.getHeader("Authorization");

        // 4. 若 Token 不存在，回應 401 錯誤
        if (token == null || token.isEmpty()) {
            log.info("令牌為空，回應 401");
            writeErrorRes(res, 401, "請先登入！");
            return false;
        }

        // 5. 若 Token 不以 Bearer 類型開頭，回應 401 錯誤，反之取 Token 部分
        if (!token.startsWith("Bearer ")) {
            writeErrorRes(res, 401, "Token 類型錯誤！");
            return false;
        }
        token = token.substring(7);

        // 6. 若 Token 存在，但校驗失敗，回應 401 錯誤
        try {
            // 解析 JWT 成功的同時
            // 取得登入用戶 ID 並存儲於當前執行緒局部變數
            Claims claims = jwtUtils.parseToken(token);
            Long userId = claims.get("id", Long.class);
            CurrentHolder.setCurrentId(userId);
        } catch (Exception e) {
            log.info("令牌不合法，回應 401");
            writeErrorRes(res, 401, "Token 無效或已過期！");
            return false;
        }

        // 7. 校驗通過，放行
        log.info("令牌合法，放行");
        return true;
    }

    // 記得加上這段，在整個請求放行結束後清除執行緒局部變數
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        CurrentHolder.clearCurrentId();
    }
}