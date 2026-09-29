package com.ternura.model.dto;

import lombok.Data;

@Data
public class WordleBeforeDailyResponse {
    private Long recordId;
    private Boolean isWin;
    private String shareToken;
}
