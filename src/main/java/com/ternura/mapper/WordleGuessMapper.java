package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.WordleGuess;
import com.ternura.model.vo.WordleGuessVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WordleGuessMapper extends BaseMapper<WordleGuess> {
    @Select("SELECT * FROM wordle_guess WHERE record_id = #{recordId} ORDER BY created_at ASC")
    List<WordleGuessVO> selectGuessesByRecordId(Long recordId);

    @Select("SELECT COUNT(1) FROM wordle_guess WHERE record_id = #{recordId}")
    int countGuessesByRecordId(Long recordId);

    @Select("SELECT COUNT(1) FROM wordle_guess WHERE record_id = #{recordId} AND guess_word = #{guessWord}")
    int countByRecordIdAndGuessWord(Long recordId, String guessWord);
}
