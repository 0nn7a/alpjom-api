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

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper; // 用於整理並統一回應格式為自定義 Result 類

    // 如果有帶 token 就解析，沒有就算了的請求路徑
    private boolean isOptionalAuthPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        // String method = req.getMethod();

        if (uri.startsWith("/wordle/share")) return true;
        if (uri.equals("/profile")) return true;
        // 之後有需要限制 method 的情況，可以這樣寫：
        // if (uri.startsWith("/games/") && "GET".equalsIgnoreCase(method)) return true;

        return false;
    }

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

        // 2. 需要直接放行的情況
        //      2-1. 註冊時已處理為不攔截登入、註冊、登出路徑 ⏳
        //      2-2. HTTP 方法是 OPTIONS 預檢的話直接放行
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }

        // 3. 獲取請求頭中的 Token
        String token = req.getHeader("Authorization");

        // 4. 若 Token 不存在，回應 401 錯誤
        if (token == null || token.isEmpty()) {
            // 4-1. 例外情況：可選 Token 之請求路徑
            if (isOptionalAuthPath(req)) {
                return true;
            }

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
            // 6-1. 同 4-1. 例外情況：可選 Token 之請求路徑
            if (isOptionalAuthPath(req)) {
                log.info("可選路徑，令牌無效但仍可放行：{}", requestURI);
                return true;
            }

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