package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.dto.WordleGuessItemResponse;
import com.ternura.model.entity.WordleGuess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WordleGuessMapper extends BaseMapper<WordleGuess> {
    @Select("SELECT guess_word, result FROM wordle_guess WHERE record_id = #{recordId} ORDER BY created_at ASC")
    List<WordleGuessItemResponse> selectGuessesByRecordId(Long recordId);

    @Select("SELECT COUNT(1) FROM wordle_guess WHERE record_id = #{recordId}")
    int countGuessesByRecordId(Long recordId);

    @Select("SELECT COUNT(1) FROM wordle_guess WHERE record_id = #{recordId} AND guess_word = #{guessWord}")
    int countByRecordIdAndGuessWord(Long recordId, String guessWord);
}
