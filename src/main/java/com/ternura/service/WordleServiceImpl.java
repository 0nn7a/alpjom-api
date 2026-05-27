package com.ternura.service;

import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.mapper.WordleGameGuessMapper;
import com.ternura.mapper.WordleGameRecordMapper;
import com.ternura.mapper.WordleWordMapper;
import com.ternura.model.dto.*;
import com.ternura.model.entity.WordleDailyAnswer;
import com.ternura.model.entity.WordleGameGuess;
import com.ternura.model.entity.WordleGameRecord;
import com.ternura.model.entity.WordleWord;
import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import com.ternura.model.vo.WordleGameGuessVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordleServiceImpl implements WordleService {
    private final WordleWordMapper wordleWordMapper;
    private final WordleGameGuessMapper wordleGameGuessMapper;
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

    @Override
    public WordleGuessResponse guess(Long userId, WordleGuessRequest request) {
        // 驗證遊戲是否存在且屬於該用戶
        WordleGameRecord record = wordleGameRecordMapper.selectByUserAndId(userId, request.getGameId());
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到符合遊戲！");
        }

        // 驗證遊戲是否還在進行中（isWin == null）
        if (record.getIsWin() != null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "該遊戲已結束，無法繼續答題！");
        }

        // 先統一將用戶猜詞轉為跟 DB 一樣全小寫
        request.setGuessWord(request.getGuessWord().toLowerCase());

        // 驗證 guess 是否為合法的 5 字母單詞（在 wordle_word 表中存在）
        WordleWord word = wordleWordMapper.selectByWord(request.getGuessWord());
        if (word == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "輸入單詞不存在，請重新嘗試！");
        }

        // 驗證是否已經猜過這個單詞
        int duplicateCount = wordleGameGuessMapper.countByGameIdAndGuessWord(record.getId(), request.getGuessWord());
        if (duplicateCount > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE_KEY, "已經猜過這個單詞了！");
        }

        // 比對每個字母的狀態，產生 result 字串
        WordleWord answer = wordleWordMapper.selectById(record.getWordId());
        String result = compareWords(answer.getWord(), request.getGuessWord());

        // 存入 wordle_game_guess
        WordleGameGuess gameGuess = new WordleGameGuess();
        gameGuess.setGameRecordId(record.getId());
        gameGuess.setGuessWord(request.getGuessWord());
        gameGuess.setResult(result);
        wordleGameGuessMapper.insert(gameGuess);

        // 判斷是否獲勝或失敗，更新 wordle_game_record
        Boolean isWin = null;
        if (answer.getWord().equalsIgnoreCase(request.getGuessWord())) {
            isWin = true;
        } else {
            int guessesCount = wordleGameGuessMapper.countGuessesByGameId(record.getId());
            if (record.getMaxGuesses() > 0 && guessesCount >= record.getMaxGuesses()) {
                isWin = false; // 別忘記 0 代表無限次數
            }
        }

        if (isWin != null) {
            record.setIsWin(isWin);
            record.setFinishedAt(LocalDateTime.now());
            wordleGameRecordMapper.updateById(record);
        }

        // 回傳結果
        WordleGuessResponse response = new WordleGuessResponse();
        response.setResult(result);
        response.setIsWin(isWin);

        if (isWin != null) {
            response.setAnswer(answer.getWord());
        }

        return response;
    }

    private String compareWords(String answer, String guess) {
        char[] result = new char[answer.length()];
        int[] answerCount = new int[26];

        // 1. 先找出所有 G：位置及字符都正確
        for (int i = 0; i < answer.length(); i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = 'G'; // 該正確的字母已用掉，剩餘次數不需要計入
            } else {
                answerCount[answer.charAt(i) - 'a']++; // 紀錄剩餘可用次數
            }
        }

        // 2. 找出 Y & W
        for (int i = 0; i < answer.length(); i++) {
            if (result[i] == 'G') continue; // 已經是 G 就跳過

            char c = guess.charAt(i);
            if (answerCount[c - 'a'] > 0) {
                result[i] = 'Y';
                answerCount[c - 'a']--; // 用掉一個可用次數
            } else {
                result[i] = 'W';
            }
        }

        return new String(result);
    }

    @Override
    public WordleGameResponse game(Long userId, Long gameId) {
        // 驗證遊戲是否存在且屬於該用戶
        WordleGameRecord record = wordleGameRecordMapper.selectByUserAndId(userId, gameId);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到符合遊戲！");
        }

        WordleGameResponse response = new WordleGameResponse();
        response.setGameId(record.getId());
        response.setMode(record.getMode());
        response.setDifficulty(record.getDifficulty());
        response.setMaxGuesses(record.getMaxGuesses());
        response.setIsWin(record.getIsWin());

        // 補齊 answer 及 guesses
        if (record.getIsWin() != null) {
            WordleWord word = wordleWordMapper.selectById(record.getWordId());
            response.setAnswer(word.getWord());
        }

        List<WordleGameGuessVO> guesses = wordleGameGuessMapper.selectGuessesByGameId(record.getId());
        response.setGuesses(guesses);

        return response;
    }
}
