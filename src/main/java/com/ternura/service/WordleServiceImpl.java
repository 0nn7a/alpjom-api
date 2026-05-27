package com.ternura.service;

import com.ternura.mapper.WordleGameRecordMapper;
import com.ternura.mapper.WordleWordMapper;
import com.ternura.model.dto.WordleStartRequest;
import com.ternura.model.dto.WordleStartResponse;
import com.ternura.model.entity.WordleDailyAnswer;
import com.ternura.model.entity.WordleGameRecord;
import com.ternura.model.entity.WordleWord;
import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class WordleServiceImpl implements WordleService {
    private final WordleWordMapper wordleWordMapper;
    private final WordleGameRecordMapper wordleGameRecordMapper;
    private final WordleDailyAnswerService wordleDailyAnswerService;

    @Override
    public WordleStartResponse start(Long userId, WordleStartRequest request) {
        if (request.getDifficulty() == null || request.getMode() == WordleMode.DAILY) {
            request.setDifficulty(WordleDifficulty.NORMAL);
        }

        // 根據 difficulty 決定 maxGuesses
        int maxGuesses = switch (request.getDifficulty()) {
            case EASY -> 0;
            case HARD -> 3;
            default -> 6;
        };

        // 建立 WordleGameRecord
        WordleGameRecord record = new WordleGameRecord();
        record.setUserId(userId);
        record.setMode(request.getMode());
        record.setDifficulty(request.getDifficulty());
        record.setMaxGuesses(maxGuesses);

        // DAILY 模式
        if (request.getMode() == WordleMode.DAILY) {
            LocalDate today = LocalDate.now(ZoneOffset.UTC); // 設定成 UTC 時區統一的今天

            // 每個用戶只能玩一局
            WordleGameRecord existed = wordleGameRecordMapper.selectTodayRecord(userId, WordleMode.DAILY, today);

            // 用戶已開始過今日謎題，直接包成 WordleStartResponse 回傳
            if (existed != null) {
                WordleStartResponse response = new WordleStartResponse();
                response.setGameId(existed.getId());
                response.setMaxGuesses(existed.getMaxGuesses());
                return response;
            }

            // 新建立或取得今日謎題
            WordleDailyAnswer answer = wordleDailyAnswerService.selectTodayAnswer();
            record.setWordId(answer.getWordId());
            record.setDate(today);
        } else if (request.getMode() == WordleMode.PRACTICE) {
            // 從單詞庫隨機選一個答案
            WordleWord word = wordleWordMapper.selectAnswerByRandom();
            record.setWordId(word.getId());
        }

        // 插入 DB 時會自動處理且回傳包含 auto id
        wordleGameRecordMapper.insert(record);

        // 回傳 WordleStartResponse
        WordleStartResponse response = new WordleStartResponse();
        response.setGameId(record.getId());
        response.setMaxGuesses(maxGuesses);
        return response;
    }
}
