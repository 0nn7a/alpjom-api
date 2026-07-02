package com.ternura.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wordle_like")
public class WordleLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gameRecordId;
    private Long userId;
    private LocalDateTime createdAt;
}
