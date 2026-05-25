package com.ternura.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ternura.model.entity.WordleWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WordleWordMapper extends BaseMapper<WordleWord> {
    // 批次插入方法
    void insertBatch(@Param("list") List<WordleWord> words);
}
