package com.ternura.service;

import com.ternura.model.dto.ProfileResponse;

public interface ProfileService {
    ProfileResponse getProfile(String username);
}
