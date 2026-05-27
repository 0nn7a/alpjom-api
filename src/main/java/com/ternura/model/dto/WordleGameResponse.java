package com.ternura.model.dto;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import com.ternura.model.vo.WordleGameGuessVO;
import lombok.Data;
import java.util.List;

@Data
public class WordleGameResponse {
    private Long gameId;
    private WordleMode mode;
    private WordleDifficulty difficulty;
    private Integer maxGuesses; // 0 == MAX
    private Boolean isWin; // 1/0 -> true/false, null -> ing
    private String answer; // 只有 isWin != null 才回傳
    private List<WordleGameGuessVO> guesses; // 該局遊戲所有猜測紀錄，依 createdAt 排序
}
