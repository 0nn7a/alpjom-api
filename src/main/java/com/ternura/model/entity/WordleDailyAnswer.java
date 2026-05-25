package com.ternura.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("wordle_daily_answer")
public class WordleDailyAnswer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long wordId;
    private LocalDate date;
}
