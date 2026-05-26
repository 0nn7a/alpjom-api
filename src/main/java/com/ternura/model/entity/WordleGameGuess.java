package com.ternura.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wordle_game_guess")
public class WordleGameGuess {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gameRecordId; // 對應的遊戲局次 id
    private String guessWord; // 用戶送出猜測的單詞
    private String result; // 標記每個字符的狀況：G全對｜Y位置錯｜W不存在
    private LocalDateTime createdAt;
}
