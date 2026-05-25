package com.ternura.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("wordle_game_record")
public class WordleGameRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long wordId; // 對應的解答單詞 id
    private WordleMode mode;
    private WordleDifficulty difficulty;
    private Integer maxGuesses; // 0 == MAX
    private Boolean isWin; // 1/0 -> true/false, null -> ing
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private LocalDate date; // 每日謎題的日期，練習模式為 null
}
