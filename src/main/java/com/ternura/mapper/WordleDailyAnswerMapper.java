package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.WordleDailyAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDate;

@Mapper
public interface WordleDailyAnswerMapper extends BaseMapper<WordleDailyAnswer> {
    @Select("SELECT * FROM wordle_daily_answer WHERE date = #{today}")
    WordleDailyAnswer selectTodayAnswer(LocalDate today);
}
