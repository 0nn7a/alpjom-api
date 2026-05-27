package com.ternura.model.dto;

import lombok.Data;

@Data
public class WordleStartResponse {
    private Long gameId;
    private Integer maxGuesses;
}
