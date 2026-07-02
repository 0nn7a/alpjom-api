package com.ternura.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.mapper.*;
import com.ternura.model.dto.*;
import com.ternura.model.entity.*;
import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleIsWin;
import com.ternura.model.enums.WordleMode;
import com.ternura.model.vo.WordleCommentVO;
import com.ternura.model.vo.WordleGameGuessVO;
import com.ternura.model.vo.WordleLikeVO;
import com.ternura.model.vo.WordleOngoingVO;
import com.ternura.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WordleServiceImpl implements WordleService {
    private final UserMapper userMapper;
    private final WordleWordMapper wordleWordMapper;
    private final WordleGameGuessMapper wordleGameGuessMapper;
    private final WordleGameRecordMapper wordleGameRecordMapper;
    private final WordleDailyAnswerService wordleDailyAnswerService;
    private final WordleLikeService wordleLikeService;
    private final WordleCommentService wordleCommentService;

    @Override
    public WordleStartResponse start(Long userId, WordleStartRequest request) {
        // DAILY 模式或未選擇時，預設難易度為 NORMAL
        if (request.getDifficulty() == null || request.getMode() == WordleMode.DAILY) {
            request.setDifficulty(WordleDifficulty.NORMAL);
        }

        // 遊玩日預設為 UTC 時區統一的今天（練習模式也固定為當日，不得擇日）
        if (request.getDate() == null || request.getMode() == WordleMode.PRACTICE) {
            request.setDate(TimeUtils.today());
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
        record.setDate(request.getDate());

        // DAILY 模式
        if (request.getMode() == WordleMode.DAILY) {
            // 每個用戶只能玩一局
            WordleGameRecord existed = wordleGameRecordMapper.selectRecordByUserModeDate(userId, WordleMode.DAILY, request.getDate());

            // 用戶已開始過當日謎題，直接包成 WordleStartResponse 回傳
            if (existed != null) {
                WordleStartResponse response = new WordleStartResponse();
                response.setGameId(existed.getId());
                response.setMaxGuesses(existed.getMaxGuesses());
                return response;
            }

            // 新建立或取得當日謎題
            WordleDailyAnswer answer = wordleDailyAnswerService.selectAnswerByDate(request.getDate());
            record.setWordId(answer.getWordId());
        }
        // PRACTICE 模式
        else if (request.getMode() == WordleMode.PRACTICE) {
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
        WordleGameRecord record = wordleGameRecordMapper.selectByIdUser(userId, request.getGameId());
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到符合遊戲！");
        }

        // 驗證遊戲是否還在進行中（isWin == null）
        if (record.getIsWin() != null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "該遊戲已結束，無法繼續答題！");
        }

        // 先統一將用戶猜詞轉為跟 DB 一樣全小寫
        request.setGuessWord(request.getGuessWord().trim().toLowerCase(Locale.ROOT));

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
            record.setShareToken(UUID.randomUUID().toString());
            record.setFinishedAt(TimeUtils.now());
            wordleGameRecordMapper.updateById(record);
        }

        // 回傳結果
        WordleGameGuessVO guess = new WordleGameGuessVO();
        guess.setGuessWord(request.getGuessWord());
        guess.setResult(result);

        WordleGuessResponse response = new WordleGuessResponse();
        response.setGuess(guess);
        response.setIsWin(isWin);

        if (isWin != null) {
            response.setAnswer(answer.getWord());
            response.setShareToken(record.getShareToken());
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
        WordleGameRecord record = wordleGameRecordMapper.selectByIdUser(userId, gameId);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到符合遊戲！");
        }

        WordleGameResponse response = new WordleGameResponse();
        response.setGameId(record.getId());
        response.setMode(record.getMode());
        response.setDifficulty(record.getDifficulty());
        response.setMaxGuesses(record.getMaxGuesses());
        response.setIsWin(record.getIsWin());
        response.setDate(record.getDate());

        // 補齊 answer、shareToken 及 guesses
        if (record.getIsWin() != null) {
            WordleWord word = wordleWordMapper.selectById(record.getWordId());
            response.setAnswer(word.getWord());
            response.setShareToken(record.getShareToken());
        }

        List<WordleGameGuessVO> guesses = wordleGameGuessMapper.selectGuessesByGameId(record.getId());
        response.setGuesses(guesses);

        return response;
    }

    @Override
    public WordleShareResponse share(Long userId, String shareToken) {
        // 根據 shareToken 查找遊戲資料
        WordleGameRecord record = wordleGameRecordMapper.selectByShareToken(shareToken);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到符合遊戲！");
        }

        // 查詢該局遊戲的用戶資料
        User user = userMapper.selectById(record.getUserId());

        // 查詢猜測過程
        List<WordleGameGuessVO> guesses = wordleGameGuessMapper.selectGuessesByGameId(record.getId());

        // 組裝、補齊回應內容
        WordleShareResponse response = new WordleShareResponse();
        response.setUsername(user.getUsername());
        response.setAvatar(user.getAvatar());
        response.setMode(record.getMode());
        response.setDifficulty(record.getDifficulty());
        response.setMaxGuesses(record.getMaxGuesses());
        response.setIsWin(record.getIsWin());
        response.setGuesses(guesses);

        // 查找、補齊 like 資料
        long likeCount = wordleLikeService.count(new LambdaQueryWrapper<WordleLike>()
                .eq(WordleLike::getGameRecordId, record.getId()));
        boolean likedByMe = false;
        if (userId != null) {
            likedByMe = wordleLikeService.getOne(new LambdaQueryWrapper<WordleLike>()
                    .eq(WordleLike::getGameRecordId, record.getId())
                    .eq(WordleLike::getUserId, userId)) != null;
        }
        WordleLikeVO wordleLike = new WordleLikeVO();
        wordleLike.setCount(likeCount);
        wordleLike.setByMe(likedByMe);
        response.setLike(wordleLike);

        // 查找、補齊留言區
        List<WordleCommentVO> comments = wordleCommentService.selectByGameId(record.getId());
        response.setComments(comments);

        return response;
    }

    @Override
    public Map<String, Object> beforeDaily(Long userId, LocalDate date) {
        // 取得該用戶當日是否開始過謎題
        if (date == null) {
            date = TimeUtils.today();
        }
        WordleGameRecord existed = wordleGameRecordMapper.selectRecordByUserModeDate(userId, WordleMode.DAILY, date);

        // 回傳 gameId、isWin 供前端判斷能否接續遊戲
        Map<String, Object> result = new HashMap<>();

        if (existed != null) {
            result.put("gameId", existed.getId());
            result.put("isWin", existed.getIsWin());
            result.put("shareToken", existed.getShareToken());
        } else {
            result.put("gameId", null);
            result.put("isWin", null);
            result.put("shareToken", null);
        }

        return result;
    }

    @Override
    public List<WordleOngoingVO> getOngoingGames(Long userId, WordleMode mode, WordleDifficulty difficulty, LocalDate date) {
        // 取得符合條件的未完成對局
        List<WordleGameRecord> records = wordleGameRecordMapper.selectByCondition(userId, mode, difficulty, WordleIsWin.ONGOING, date);

        // 封裝上每場對局的已猜測次數並回傳
        return records.stream().map(record -> {
            WordleOngoingVO ongoing = new WordleOngoingVO();
            ongoing.setGameId(record.getId());
            ongoing.setMode(record.getMode());
            ongoing.setDifficulty(record.getDifficulty());
            ongoing.setMaxGuesses(record.getMaxGuesses());
            ongoing.setCreatedAt(record.getCreatedAt());

            int guessCount = wordleGameGuessMapper.countGuessesByGameId(record.getId());
            ongoing.setCurrentGuesses(guessCount);

            return ongoing;
        }).toList();
    }
}
