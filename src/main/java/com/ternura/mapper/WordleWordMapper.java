package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.WordleWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WordleWordMapper extends BaseMapper<WordleWord> {
    // 批次插入方法
    void insertBatch(@Param("list") List<WordleWord> words);

    // 隨機取得一個謎底
    @Select("SELECT * FROM wordle_word WHERE is_answer_candidate = 1 ORDER BY RAND() LIMIT 1")
    WordleWord selectAnswerByRandom();

    // 驗證 guessWord 是否為合法單詞
    @Select("SELECT * FROM wordle_word WHERE word = #{guessWord}")
    WordleWord selectByWord(String guessWord);
}
