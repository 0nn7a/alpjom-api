package com.ternura.model.dto;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WordleStartRequest {
    @NotNull(message = "請選擇遊戲模式！")
    private WordleMode mode;
    private WordleDifficulty difficulty;
}
