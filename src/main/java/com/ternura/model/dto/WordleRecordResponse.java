package com.ternura.model.dto;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WordleRecordResponse {
    private Long id;
    private WordleMode mode;
    private WordleDifficulty difficulty;
    private Boolean isWin;
    private String shareToken;
    private LocalDateTime finishedAt;
}
