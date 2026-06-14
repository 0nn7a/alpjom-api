package com.ternura.controller;

import com.ternura.model.common.Result;
import com.ternura.model.dto.ProfileResponse;
import com.ternura.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/{username}")
    public Result<ProfileResponse> getProfile(@PathVariable String username){
        ProfileResponse response = profileService.getProfile(username);
        return Result.success(response);
    }
}
