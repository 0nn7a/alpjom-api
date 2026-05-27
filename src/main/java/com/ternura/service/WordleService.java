package com.ternura.service;

import com.ternura.model.dto.*;

public interface WordleService{
    WordleStartResponse start(Long userId, WordleStartRequest request);
    WordleGuessResponse guess(Long userId, WordleGuessRequest request);
    WordleGameResponse game(Long userId, Long gameId);
}
