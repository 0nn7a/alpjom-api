package com.ternura.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.UserAvatar;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserAvatarMapper extends BaseMapper<UserAvatar> {
    @Select("SELECT * FROM user_avatar WHERE user_id = #{userId} AND id = #{id}")
    UserAvatar selectByUserId(Long userId, Long id);

    List<UserAvatar> selectByUserIds(Long userId, List<Long> ids);

    void deleteByUserIds(Long userId, List<Long> ids);
}
