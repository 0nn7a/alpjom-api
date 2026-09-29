package com.ternura.service;

import com.ternura.model.dto.*;
import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;

import java.time.LocalDate;
import java.util.List;

public interface WordleService{
    WordleStartResponse start(Long userId, WordleStartRequest request);

    WordleGuessResponse guess(Long userId, WordleGuessRequest request);

    WordleGameResponse game(Long userId, Long recordId);

    WordleShareResponse share(Long userId, String shareToken);

    WordleBeforeDailyResponse beforeDaily(Long userId, LocalDate date);

    List<WordleOngoingResponse> getOngoingGames(Long userId, WordleMode mode, WordleDifficulty difficulty, LocalDate date);
}
