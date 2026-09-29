package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.dto.UserResponse;
import com.ternura.model.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
    List<UserResponse> selectFollowersByUserId(Long userId);
    List<UserResponse> selectFollowingsByUserId(Long userId);
}
