package com.ternura.model.dto;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WordleOngoingResponse {
    private Long recordId;
    private WordleMode mode;
    private WordleDifficulty difficulty;
    private Integer maxGuesses;
    private Integer currentGuesses;
    private LocalDateTime createdAt;
}
