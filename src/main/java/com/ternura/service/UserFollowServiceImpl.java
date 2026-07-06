package com.ternura.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.mapper.UserFollowMapper;
import com.ternura.mapper.UserMapper;
import com.ternura.model.dto.UserFollowResponse;
import com.ternura.model.entity.User;
import com.ternura.model.entity.UserFollow;
import com.ternura.model.vo.UserVO;
import com.ternura.utils.TimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {
    private final UserMapper userMapper;
    private final UserFollowMapper userFollowMapper;

    private UserFollowResponse countFollow(Long targetUserId, boolean isFollowing) {
        long followerCount = count(new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowingId, targetUserId));
        long followingCount = count(new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowerId, targetUserId));

        UserFollowResponse response = new UserFollowResponse();
        response.setFollowerCount(followerCount);
        response.setFollowingCount(followingCount);
        response.setFollowing(isFollowing);
        return response;
    }

    @Override
    public UserFollowResponse getFollow(Long targetUserId, Long currentUserId) {
        boolean isFollowing = false;
        if (currentUserId != null) {
            isFollowing = getOne(new LambdaQueryWrapper<UserFollow>()
                    .eq(UserFollow::getFollowerId, currentUserId)
                    .eq(UserFollow::getFollowingId, targetUserId)) != null;
        }
        return countFollow(targetUserId, isFollowing);
    }

    @Override
    public List<UserVO> getFollower(String username) {
        // 確認被查詢的使用者存在
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "查無此用戶！");
        }

        // 查找該使用者的所有粉絲
        return userFollowMapper.selectFollowersByUserId(user.getId());
    }

    @Override
    public List<UserVO> getFollowing(String username) {
        // 確認被查詢的使用者存在
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "查無此用戶！");
        }

        // 查找該使用者的所有粉絲
        return userFollowMapper.selectFollowingsByUserId(user.getId());
    }

    @Override
    public UserFollowResponse toggleFollow(Long followerId, String followingUsername) {
        // 確認被追蹤者存在
        User following = userMapper.selectByUsername(followingUsername);
        if (following == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "目標使用者不存在！");
        }

        // 禁止追蹤自己
        long followingId = following.getId();
        if (followerId.equals(followingId)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "不可以追蹤自己！");
        }

        // 切換追蹤狀態
        UserFollow existed = getOne(new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowerId, followerId)
                .eq(UserFollow::getFollowingId, followingId));

        if (existed != null) {
            removeById(existed.getId());
        } else {
            UserFollow follow = new UserFollow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            follow.setCreatedAt(TimeUtils.now());
            save(follow);
        }

        // 計算當前查看使用者的粉絲及追蹤中總數，及登入用戶對其的追蹤狀態
        return countFollow(followingId, existed == null);
    }
}
