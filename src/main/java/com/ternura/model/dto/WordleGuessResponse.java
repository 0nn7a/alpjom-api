package com.ternura.model.dto;

import lombok.Data;

@Data
public class WordleGuessResponse {
    private WordleGuessItemResponse guess;
    private Boolean isWin;     // 1/0 -> true/false, null -> ing
    private String answer;     // 只在遊戲結束後回傳，避免失敗後不知道答案
    private String shareToken; // 只在遊戲結束後回傳，能順手導向至分享頁
}
