package com.ternura.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wordle_word")
public class WordleWord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String word;
    private Integer length;
    private Boolean isAnswerCandidate; // 1/0 auto -> true/false
}
