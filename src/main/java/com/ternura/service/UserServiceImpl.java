package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.mapper.UserMapper;
import com.ternura.model.dto.LoginRequest;
import com.ternura.model.dto.LoginResponse;
import com.ternura.model.dto.RegisterRequest;
import com.ternura.model.entity.RefreshToken;
import com.ternura.model.entity.User;
import com.ternura.model.vo.UserVO;
import com.ternura.utils.JwtUtils;
import com.ternura.utils.PasswordUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    // 繼承 MyBatisPlus 的通用實作類：
    // ServiceImpl<UserMapper, User> 代表用 UserMapper 操作 DB 的 User 表

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void register(RegisterRequest request) {
        List<String> existed = new ArrayList<>();

        // 檢查 username、email 是否重複
        if (userMapper.findByUsername(request.getUsername()) != null) {
            existed.add("username");
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            existed.add("email");
        }
        if (!existed.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("existed", existed);
            throw new BusinessException(ErrorCode.DUPLICATE_KEY, "username 或 email 已存在！" ,data);
        }

        // 加鹽密碼
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(request.getPassword(), salt);

        // 組裝 Entity 存入 DB: 跳過 avatar 頭像預設 null
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        user.setSalt(salt);

        // 繼承了 MyBatisPlus 的通用實作類後
        // 自動就有的方法之一：save(), saveBatch(), getById(), list(), updateById(), removeById()...
        save(user);
        // 這裏實際上就是去 UserMapper 調用 insert() 方法（同樣繼承自 BaseMapper）
        // 檢查 entity 是否為 null -> INSERT 語句 -> 自動回填 AUTO_INCREMENT id -> 回傳 boolean
        // 因此甚至能在這個階段使用 user.gerId()
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // 檢查是否已註冊
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "此 Email 尚未註冊！");
        }

        // 驗證密碼
        boolean isMatch = PasswordUtil.verify(password, user.getSalt(), user.getPassword());
        if (!isMatch) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "密碼錯誤！");
        }

        // 產生 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());

        String token = jwtUtils.generateToken(claims);
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());
        LocalDateTime expiredAt = jwtUtils.parseRefreshTokenExpiration(refreshToken);

        // 清除該用戶已過期的 refresh token 資料
        refreshTokenService.deleteExpiredTokensByUserId(user.getId());

        // 存入新的一筆 refresh token 到 DB
        RefreshToken entity = new RefreshToken();
        entity.setUserId(user.getId());
        entity.setToken(refreshToken);
        entity.setExpiredAt(expiredAt);
        refreshTokenService.save(entity);

        // 回傳 User 非敏感資料 + 兩個 Token 資料
        UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getEmail(), user.getAvatar());
        LoginResponse response = new LoginResponse();
        response.setUser(userVO);
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiredAt(jwtUtils.parseTokenTime(token));
        response.setRefreshExpiredAt(jwtUtils.parseRefreshTokenTime(refreshToken));

        return response;
    }

    @Override
    public void logout(String refreshToken) {
        try {
            Long userId = jwtUtils.parseRefreshToken(refreshToken).get("id", Long.class);
            refreshTokenService.deleteByTokenAndUserId(refreshToken, userId);
        } catch (JwtException e) {
            // 因 refresh token 無效或過期導致的解析錯誤
            // 但對用戶而言登出都必須是成功的
            // 所以這裏只嘗試刪除用戶傳遞來的 refresh token，不順便清除該用戶的過期資料
            refreshTokenService.deleteByToken(refreshToken);
        }
    }
}
