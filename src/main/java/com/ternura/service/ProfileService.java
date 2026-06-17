package com.ternura.service;

import com.ternura.model.dto.AvatarDeleteRequest;
import com.ternura.model.dto.ProfileRequest;
import com.ternura.model.dto.ProfileResponse;
import com.ternura.model.entity.UserAvatar;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProfileService {
    ProfileResponse getProfile(String username);
    void updateProfile(Long userId, ProfileRequest request);

    List<UserAvatar> getAvatar(Long userId);
    UserAvatar uploadAvatar(Long userId, MultipartFile file);
    void deleteAvatar(Long userId, AvatarDeleteRequest request);
}
