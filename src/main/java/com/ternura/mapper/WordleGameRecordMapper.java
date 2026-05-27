package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.WordleGameRecord;
import com.ternura.model.enums.WordleMode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface WordleGameRecordMapper extends BaseMapper<WordleGameRecord> {
    @Select("SELECT * FROM wordle_game_record WHERE user_id = #{userId} AND mode = #{mode} AND date = #{today}")
    WordleGameRecord selectTodayRecord(Long userId, WordleMode mode, LocalDate today);
}
