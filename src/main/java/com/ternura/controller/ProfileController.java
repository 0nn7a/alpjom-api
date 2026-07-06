package com.ternura.controller;

import com.ternura.model.common.Result;
import com.ternura.model.dto.*;
import com.ternura.model.entity.UserAvatar;
import com.ternura.model.vo.UserVO;
import com.ternura.service.ProfileService;
import com.ternura.service.UserFollowService;
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
    private final UserFollowService userFollowService;

    @GetMapping
    public Result<ProfileResponse> getProfile(@RequestParam @NotBlank(message = "用戶名不得為空！") String username){
        Long userId = CurrentHolder.getCurrentId();
        ProfileResponse response = profileService.getProfile(userId, username);
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

    @GetMapping("/follow/follower/{username}")
    public Result<List<UserVO>> getFollower(@PathVariable String username){
        List<UserVO> response = userFollowService.getFollower(username);
        return Result.success(response);
    }

    @GetMapping("/follow/following/{username}")
    public Result<List<UserVO>> getFollowing(@PathVariable String username){
        List<UserVO> response = userFollowService.getFollowing(username);
        return Result.success(response);
    }

    @PostMapping("/follow/{followingUsername}")
    public Result<UserFollowResponse> follow(@PathVariable String followingUsername){
        Long userId = CurrentHolder.getCurrentId();
        UserFollowResponse response = userFollowService.toggleFollow(userId, followingUsername);
        return Result.success(response);
    }
}
