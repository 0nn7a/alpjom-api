package com.ternura.model.dto;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
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
    private List<WordleGuessItemResponse> guesses; // 該局遊戲所有猜測紀錄，依 createdAt 排序
    private WordleLikeResponse like;
    private List<WordleCommentResponse> comments; // 該局分享留言區，依 createdAt 排序
}
