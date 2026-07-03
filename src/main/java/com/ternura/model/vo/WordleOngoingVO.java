package com.ternura.model.vo;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WordleOngoingVO {
    private Long recordId;
    private WordleMode mode;
    private WordleDifficulty difficulty;
    private Integer maxGuesses;
    private Integer currentGuesses;
    private LocalDateTime createdAt;
}
