package com.ternura.controller;

import com.ternura.model.common.Result;
import com.ternura.model.dto.*;
import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import com.ternura.model.vo.WordleLikeVO;
import com.ternura.model.vo.WordleOngoingVO;
import com.ternura.service.WordleCommentService;
import com.ternura.service.WordleLikeService;
import com.ternura.service.WordleService;
import com.ternura.utils.CurrentHolder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/wordle")
@RequiredArgsConstructor
public class WordleController {
    private final WordleService wordleService;
    private final WordleCommentService wordleCommentService;
    private final WordleLikeService wordleLikeService;

    @PostMapping("/start")
    public Result<WordleStartResponse> start(@Valid @RequestBody WordleStartRequest request) {
        Long userId = CurrentHolder.getCurrentId();
        WordleStartResponse response = wordleService.start(userId, request);
        return Result.success(response);
    }

    @PostMapping("/guess")
    public Result<WordleGuessResponse> guess(@Valid @RequestBody WordleGuessRequest request) {
        Long userId = CurrentHolder.getCurrentId();
        WordleGuessResponse response = wordleService.guess(userId, request);
        return Result.success(response);
    }

    @GetMapping("/game/{gameId}")
    public Result<WordleGameResponse> game(@PathVariable Long gameId) {
        Long userId = CurrentHolder.getCurrentId();
        WordleGameResponse response = wordleService.game(userId, gameId);
        return Result.success(response);
    }

    @GetMapping("/share/{shareToken}")
    public Result<WordleShareResponse> share(@PathVariable String shareToken) {
        Long userId = CurrentHolder.getCurrentId();
        WordleShareResponse response = wordleService.share(userId, shareToken);
        return Result.success(response);
    }

    @PostMapping("/like/{shareToken}")
    public Result<WordleLikeVO> like(@PathVariable String shareToken) {
        Long userId = CurrentHolder.getCurrentId();
        WordleLikeVO response = wordleLikeService.toggle(userId, shareToken);
        return Result.success(response);
    }

    @PostMapping("/comment")
    public Result<Void> insertComment(@Valid @RequestBody WordleCommentRequest request) {
        Long userId = CurrentHolder.getCurrentId();
        wordleCommentService.insert(userId, request);
        return Result.success();
    }

    @DeleteMapping("/comment/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        Long userId = CurrentHolder.getCurrentId();
        wordleCommentService.delete(userId, id);
        return Result.success();
    }

    @GetMapping("/before/daily")
    public Result<Map<String, Object>> beforeDaily(@RequestParam(required = false) LocalDate date) {
        Long userId = CurrentHolder.getCurrentId();
        Map<String, Object> response = wordleService.beforeDaily(userId, date);
        return Result.success(response);
    }

    @GetMapping("/before/practice")
    public Result<List<WordleOngoingVO>> beforePractice(@RequestParam @NotNull(message = "請選擇難易度！") WordleDifficulty difficulty) {
        Long userId = CurrentHolder.getCurrentId();
        List<WordleOngoingVO> games = wordleService.getOngoingGames(userId, WordleMode.PRACTICE, difficulty, null);
        return Result.success(games);
    }

}
