package com.ternura.service;

import com.ternura.model.dto.*;
import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import com.ternura.model.vo.WordleOngoingVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WordleService{
    WordleStartResponse start(Long userId, WordleStartRequest request);

    WordleGuessResponse guess(Long userId, WordleGuessRequest request);

    WordleGameResponse game(Long userId, Long gameId);

    WordleShareResponse share(Long userId, String shareToken);

    Map<String, Object> beforeDaily(Long userId, LocalDate date);

    List<WordleOngoingVO> getOngoingGames(Long userId, WordleMode mode, WordleDifficulty difficulty, LocalDate date);
}
