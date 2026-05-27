package com.ternura.model.dto;

import lombok.Data;

@Data
public class WordleGuessRequest {
    private Long gameId;
    private String guessWord;
}
