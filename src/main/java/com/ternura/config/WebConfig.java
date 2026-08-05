package com.ternura.config;

import com.ternura.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final TokenInterceptor tokenInterceptor;

    // 註冊攔截器，並指定攔截 / 排除的路徑
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 攔截器的執行順序會依照註冊的先後順序
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")                 // 攔截所有路徑
                .excludePathPatterns(                   // 直接排除不需攔截的路徑
                        "/actuator/health",             // 放行健康檢查免登入，供監控與部署驗證
                        "/auth/**",
                        "/profile/follow/list/**",
                        "/game/**");
    }
}