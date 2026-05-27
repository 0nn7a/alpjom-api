package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.WordleGameGuess;
import com.ternura.model.vo.WordleGameGuessVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WordleGameGuessMapper extends BaseMapper<WordleGameGuess> {
    @Select("SELECT * FROM wordle_game_guess WHERE game_record_id = #{gameId} ORDER BY created_at ASC")
    List<WordleGameGuessVO> selectGuessesByGameId(Long gameId);

    @Select("SELECT COUNT(1) FROM wordle_game_guess WHERE game_record_id = #{gameId}")
    int countGuessesByGameId(Long gameId);

    @Select("SELECT COUNT(1) FROM wordle_game_guess WHERE game_record_id = #{gameId} AND guess_word = #{guessWord}")
    int countByGameIdAndGuessWord(Long gameId, String guessWord);
}
