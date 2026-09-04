package com.ternura.controller;

import com.ternura.model.common.Result;
import com.ternura.model.dto.*;
import com.ternura.model.entity.UserAvatar;
import com.ternura.model.vo.UserVO;
import com.ternura.service.ProfileService;
import com.ternura.service.UserFollowService;
import com.ternura.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;
    private final UserFollowService userFollowService;

    @GetMapping
    public Result<ProfileResponse> getProfile(
            @AuthenticationPrincipal Long userId,
            @RequestParam @NotBlank(message = "用戶名不得為空！") String username,
            PageRequest pageRequest){
        ProfileResponse response = profileService.getProfile(userId, username, pageRequest);
        return Result.success(response);
    }

    @PatchMapping("/user")
    public Result<Void> updateUser(
            @AuthenticationPrincipal Long userId,
            @RequestBody ProfileRequest request) {
        userService.updateUser(userId, request);
        return Result.success();
    }

    @PatchMapping("/password")
    public Result<Void> updatePassword(
            @AuthenticationPrincipal Long userId,
            @RequestBody PasswordRequest request) {
        userService.updatePassword(userId, request);
        return Result.success();
    }

    @GetMapping("/avatar")
    public Result<List<UserAvatar>> getAvatar(@AuthenticationPrincipal Long userId) {
        List<UserAvatar> response = profileService.getAvatar(userId);
        return Result.success(response);
    }

    @PostMapping("/avatar")
    public Result<UserAvatar> uploadAvatar(
            @AuthenticationPrincipal Long userId,
            @RequestParam MultipartFile file){
        UserAvatar response = profileService.uploadAvatar(userId, file);
        return Result.success(response);
    }

    @DeleteMapping("/avatar")
    public Result<Void> deleteAvatar(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid AvatarDeleteRequest request){
        profileService.deleteAvatar(userId, request);
        return Result.success();
    }

    @GetMapping("/follow/list/follower/{username}")
    public Result<List<UserVO>> getFollower(@PathVariable String username){
        List<UserVO> response = userFollowService.getFollower(username);
        return Result.success(response);
    }

    @GetMapping("/follow/list/following/{username}")
    public Result<List<UserVO>> getFollowing(@PathVariable String username){
        List<UserVO> response = userFollowService.getFollowing(username);
        return Result.success(response);
    }

    @PostMapping("/follow/{followingUsername}")
    public Result<UserFollowResponse> follow(
            @AuthenticationPrincipal Long userId,
            @PathVariable String followingUsername){
        UserFollowResponse response = userFollowService.toggleFollow(userId, followingUsername);
        return Result.success(response);
    }
}
