package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.WordleGameGuess;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WordleGameGuessMapper extends BaseMapper<WordleGameGuess> {
}
