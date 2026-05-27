package com.ternura.model.dto;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import lombok.Data;

@Data
public class WordleStartRequest {
    private WordleMode mode;
    private WordleDifficulty difficulty;
}
