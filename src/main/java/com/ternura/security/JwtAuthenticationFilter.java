package com.ternura.security;

import com.ternura.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 用於在 request 上暫存失敗原因，讓 RestAuthenticationEntryPoint 統一回應
    public static final String AUTH_ERROR_ATTR = "authError";

    private final JwtUtils jwtUtils;

    // 繼承 OncePerRequestFilter 並重寫 doFilterInternal
    // 能確保同一個請求只執行一次核心邏輯
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        // 沒帶 Token：直接放行，交給後面的授權規則決定是否需要登入
        if (header != null && !header.isBlank()) {
            if (!header.startsWith("Bearer ")) {
                request.setAttribute(AUTH_ERROR_ATTR, "Token 類型錯誤！");
            } else {
                String token = header.substring(7);
                try {
                    Claims claims = jwtUtils.parseToken(token);
                    Long userId = claims.get("id", Long.class);

                    // 包裝成 Spring Security 的驗證結果物件，參數分別為：
                    // principal 識別身分的主體
                    // credentials 通常用密碼作為憑證（這裡已用 JWT 驗證完畢）
                    // authorities 權限列表（暫時沒有用到角色）
                    var authentication = new UsernamePasswordAuthenticationToken(userId, null, List.of());

                    // 附加這次請求的環境資訊到這個驗證結果上，主要包含：
                    // remoteAddress：發起請求的來源 IP
                    // sessionId：如果有 Session 的話（但專案是無狀態 REST API，這裡通常會是 null）
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 將驗證結果物件實際存放到 SecurityContextHolder 裡
                    // 底層一樣是靠 ThreadLocal 實作，方便後續取得身分
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 之後只需要在 Controller 參數裡使用 @AuthenticationPrincipal Long userId，就等於：
                    // -Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    // -Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
                } catch (Exception e) {
                    request.setAttribute(AUTH_ERROR_ATTR, "Token 無效或已過期！");
                }
            }
        }

        // 當前 Filter 工作完成，交給下一個 Filter 繼續 = Filter Chain
        filterChain.doFilter(request, response);
    }
}