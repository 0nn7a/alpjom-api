package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.WordleComment;
import com.ternura.model.vo.WordleCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WordleCommentMapper extends BaseMapper<WordleComment> {
    @Update("UPDATE wordle_comment SET is_deleted = 1, updated_at = #{updatedAt} WHERE id = #{id}")
    int softDeleteById(Long id, LocalDateTime updatedAt);

    List<WordleCommentVO> selectByGameId(Long gameId);
}
