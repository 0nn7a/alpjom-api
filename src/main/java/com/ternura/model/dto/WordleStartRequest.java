package com.ternura.model.dto;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WordleStartRequest {
    @NotNull(message = "請選擇遊戲模式！")
    private WordleMode mode;
    private WordleDifficulty difficulty;
    private LocalDate date;
}
