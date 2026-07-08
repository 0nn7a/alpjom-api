package com.ternura.service;

import com.ternura.model.dto.AvatarDeleteRequest;
import com.ternura.model.dto.PageRequest;
import com.ternura.model.dto.ProfileResponse;
import com.ternura.model.entity.UserAvatar;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProfileService {
    ProfileResponse getProfile(Long userId, String username, PageRequest pageRequest);

    List<UserAvatar> getAvatar(Long userId);
    UserAvatar uploadAvatar(Long userId, MultipartFile file);
    void deleteAvatar(Long userId, AvatarDeleteRequest request);
}
