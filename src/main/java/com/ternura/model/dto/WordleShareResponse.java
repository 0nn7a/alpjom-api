package com.ternura.model.dto;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import com.ternura.model.vo.WordleCommentVO;
import com.ternura.model.vo.WordleGameGuessVO;
import lombok.Data;

import java.util.List;

@Data
public class WordleShareResponse {
    private String username;
    private String avatar;
    private WordleMode mode;
    private WordleDifficulty difficulty;
    private Integer maxGuesses; // 0 == MAX
    private Boolean isWin;      // 1/0 -> true/false
    private List<WordleGameGuessVO> guesses; // 該局遊戲所有猜測紀錄，依 createdAt 排序
    private List<WordleCommentVO> comments; // 該局分享留言區，依 createdAt 排序
}
