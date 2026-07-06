package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ternura.model.dto.UserFollowResponse;
import com.ternura.model.entity.UserFollow;
import com.ternura.model.vo.UserVO;

import java.util.List;


public interface UserFollowService extends IService<UserFollow> {
    UserFollowResponse getFollow(Long targetUserId, Long currentUserId);
    List<UserVO> getFollower(String username);
    List<UserVO> getFollowing(String username);
    UserFollowResponse toggleFollow(Long followerId, String followingUsername);
}
