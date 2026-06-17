package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 繼承 MyBatisPlus 的通用實作類 BaseMapper<User> ：
    // 就能使用 insert(), deleteByIds(), update(), selectByIds()... 等方法

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(String email);

    @Update("UPDATE user SET avatar = DEFAULT(avatar) WHERE id = #{id}")
    void resetAvatarToDefault(Long id);
}
