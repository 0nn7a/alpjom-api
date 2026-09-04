package com.ternura.config;

import com.ternura.security.JwtAuthenticationFilter;
import com.ternura.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)        // 無狀態 API 不需要 CSRF
                .cors(Customizer.withDefaults())              // 跨域使用下方 corsConfigurationSource 的設定
                .formLogin(AbstractHttpConfigurer::disable)   // 關閉 Spring Security 內建的「表單登入」機制
                .httpBasic(AbstractHttpConfigurer::disable)   // 關閉 HTTP Basic Authentication
                .anonymous(AbstractHttpConfigurer::disable)   // 避免匿名使用者塞入 String principal，保留 null

                // Spring Security 預設會依賴 HttpSession 來記住「這個使用者已經登入過了」
                // 就像傳統網站登入後，瀏覽器帶著 Session Cookie，後續請求就不用再驗證一次
                // 但專案目前每次請求都是靠 JWT 重新驗證身分，伺服器完全不記得任何登入狀態，故設定為「STATELESS 無狀態」
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 註冊「驗證失敗時該找誰處理」
                .exceptionHandling(eh -> eh.authenticationEntryPoint(restAuthenticationEntryPoint))

                // 定義每個路徑的授權規則，會依照寫的順序逐條比對規則
                .authorizeHttpRequests(auth -> auth
                        // 完全公開：不需要登入
                        .requestMatchers(
                                "/actuator/health",
                                "/auth/**",
                                "/profile/follow/list/**",
                                "/game/**").permitAll()

                        // 可選登入：有帶 Token 就解析，沒帶也放行（目前僅限 GET 方法）
                        .requestMatchers(HttpMethod.GET,
                                "/profile",
                                "/wordle/share/**").permitAll()

                        // 其餘一律需要登入
                        .anyRequest().authenticated()
                )

                // 插入到整個 Filter Chain 的特定位置，避免授權判斷先跑而被誤判成「未登入」
                // 這裡就是將 jwtAuthenticationFilter 插到 UsernamePasswordAuthenticationFilter 之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 把上述設定「組裝」成一個真正的 SecurityFilterChain 物件
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "https://0nn7a.github.io"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // 瀏覽器預檢結果快取 3600s，期間內同請求不會再發 OPTIONS

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}