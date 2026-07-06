package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.UserFollow;
import com.ternura.model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
    List<UserVO> selectFollowersByUserId(Long userId);
    List<UserVO> selectFollowingsByUserId(Long userId);
}
