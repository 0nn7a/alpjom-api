package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.WordleGameRecord;
import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleIsWin;
import com.ternura.model.enums.WordleMode;
import com.ternura.model.vo.HeatmapVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WordleGameRecordMapper extends BaseMapper<WordleGameRecord> {
    @Select("SELECT * FROM wordle_game_record WHERE user_id = #{userId} AND mode = #{mode} AND date = #{today}")
    WordleGameRecord selectRecordByUserModeDate(Long userId, WordleMode mode, LocalDate today);

    @Select("SELECT * FROM wordle_game_record WHERE id = #{id} AND user_id = #{userId}")
    WordleGameRecord selectByIdUser(Long userId, Long id);

    @Select("SELECT * FROM wordle_game_record WHERE share_token = #{shareToken}")
    WordleGameRecord selectByShareToken(String shareToken);

    List<WordleGameRecord> selectByCondition(Long userId, WordleMode mode, WordleDifficulty difficulty, WordleIsWin isWin, LocalDate date);


    // Profile Data
    @Select("SELECT COUNT(1) > 0 FROM wordle_game_record WHERE user_id = #{userId} AND mode = 'DAILY' AND is_win IS NOT NULL AND date = #{today}")
    boolean isDailyDoneByUserDate(Long userId, LocalDate today);

    @Select("SELECT COUNT(1) FROM wordle_game_record WHERE user_id = #{userId} AND is_win IS NOT NULL")
    int countTotalDoneByUser(Long userId);

    List<HeatmapVO> selectHeatmapByUser(Long userId);
}
