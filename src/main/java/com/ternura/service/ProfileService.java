package com.ternura.service;

import com.ternura.model.dto.UserAvatarDeleteRequest;
import com.ternura.model.dto.PageRequest;
import com.ternura.model.dto.ProfileResponse;
import com.ternura.model.dto.UserAvatarResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProfileService {
    ProfileResponse getProfile(Long userId, String username, PageRequest pageRequest);

    List<UserAvatarResponse> getAvatar(Long userId);
    UserAvatarResponse uploadAvatar(Long userId, MultipartFile file);
    void deleteAvatar(Long userId, UserAvatarDeleteRequest request);
}
