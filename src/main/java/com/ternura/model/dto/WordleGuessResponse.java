package com.ternura.model.dto;

import lombok.Data;

@Data
public class WordleGuessResponse {
    private String result;  // G ✅| Y ⚠️| W ❌
    private Boolean isWin;  // 1/0 -> true/false, null -> ing
    private String answer;  // 只在遊戲結束後回傳，避免失敗後不知道答案
}
