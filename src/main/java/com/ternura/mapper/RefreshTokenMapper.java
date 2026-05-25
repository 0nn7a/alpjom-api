package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.RefreshToken;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RefreshTokenMapper extends BaseMapper<RefreshToken> {
    @Delete("DELETE FROM refresh_token WHERE token = #{token}")
    void deleteByToken(String token);

    @Delete("DELETE FROM refresh_token WHERE token = #{token} OR (user_id = #{userId} AND expired_at < NOW())")
    void deleteByTokenAndUserId(String token, Long userId);

    @Delete("DELETE FROM refresh_token WHERE user_id = #{userId} AND expired_at < NOW()")
    void deleteExpiredTokensByUserId(Long userId);

    @Select("SELECT * FROM refresh_token WHERE token = #{token}")
    RefreshToken selectByToken(String token);
}
