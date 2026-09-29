package com.ternura.model.dto;

import lombok.Data;

@Data
public class WordleGuessItemResponse {
    private String guessWord;
    private String result;
}
