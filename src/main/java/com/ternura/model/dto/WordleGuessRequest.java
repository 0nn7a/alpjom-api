package com.ternura.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WordleGuessRequest {
    @NotNull(message = "請提供遊戲 ID！")
    private Long gameId;

    @NotBlank(message = "請輸入猜測單詞！")
    private String guessWord;
}
