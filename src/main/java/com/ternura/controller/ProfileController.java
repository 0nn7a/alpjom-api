package com.ternura.controller;

import com.ternura.model.common.Result;
import com.ternura.model.dto.AvatarDeleteRequest;
import com.ternura.model.dto.PasswordRequest;
import com.ternura.model.dto.ProfileRequest;
import com.ternura.model.dto.ProfileResponse;
import com.ternura.model.entity.UserAvatar;
import com.ternura.service.ProfileService;
import com.ternura.service.UserService;
import com.ternura.utils.CurrentHolder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;

    @GetMapping
    public Result<ProfileResponse> getProfile(@RequestParam @NotBlank(message = "用戶名不得為空！") String username){
        ProfileResponse response = profileService.getProfile(username);
        return Result.success(response);
    }

    @PatchMapping("/user")
    public Result<Void> updateUser(@RequestBody ProfileRequest request) {
        Long userId = CurrentHolder.getCurrentId();
        userService.updateUser(userId, request);
        return Result.success();
    }

    @PatchMapping("/password")
    public Result<Void> updatePassword(@RequestBody PasswordRequest request) {
        Long userId = CurrentHolder.getCurrentId();
        userService.updatePassword(userId, request);
        return Result.success();
    }

    @GetMapping("/avatar")
    public Result<List<UserAvatar>> getAvatar() {
        Long userId = CurrentHolder.getCurrentId();
        List<UserAvatar> response = profileService.getAvatar(userId);
        return Result.success(response);
    }

    @PostMapping("/avatar")
    public Result<UserAvatar> uploadAvatar(@RequestParam MultipartFile file){
        Long userId = CurrentHolder.getCurrentId();
        UserAvatar response = profileService.uploadAvatar(userId, file);
        return Result.success(response);
    }

    @DeleteMapping("/avatar")
    public Result<Void> deleteAvatar(@RequestBody @Valid AvatarDeleteRequest request){
        Long userId = CurrentHolder.getCurrentId();
        profileService.deleteAvatar(userId, request);
        return Result.success();
    }
}
