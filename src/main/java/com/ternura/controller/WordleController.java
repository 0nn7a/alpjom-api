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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Result<WordleStartResponse> start(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WordleStartRequest request) {
        WordleStartResponse response = wordleService.start(userId, request);
        return Result.success(response);
    }

    @PostMapping("/guess")
    public Result<WordleGuessResponse> guess(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WordleGuessRequest request) {
        WordleGuessResponse response = wordleService.guess(userId, request);
        return Result.success(response);
    }

    @GetMapping("/game/{recordId}")
    public Result<WordleGameResponse> game(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long recordId) {
        WordleGameResponse response = wordleService.game(userId, recordId);
        return Result.success(response);
    }

    @GetMapping("/share/{shareToken}")
    public Result<WordleShareResponse> share(
            @AuthenticationPrincipal Long userId,
            @PathVariable String shareToken) {
        WordleShareResponse response = wordleService.share(userId, shareToken);
        return Result.success(response);
    }

    @PostMapping("/like/{shareToken}")
    public Result<WordleLikeVO> like(
            @AuthenticationPrincipal Long userId,
            @PathVariable String shareToken) {
        WordleLikeVO response = wordleLikeService.toggle(userId, shareToken);
        return Result.success(response);
    }

    @PostMapping("/comment")
    public Result<Void> insertComment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WordleCommentRequest request) {
        wordleCommentService.insert(userId, request);
        return Result.success();
    }

    @DeleteMapping("/comment/{id}")
    public Result<Void> deleteComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        wordleCommentService.delete(userId, id);
        return Result.success();
    }

    @GetMapping("/before/daily")
    public Result<Map<String, Object>> beforeDaily(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) LocalDate date) {
        Map<String, Object> response = wordleService.beforeDaily(userId, date);
        return Result.success(response);
    }

    @GetMapping("/before/practice")
    public Result<List<WordleOngoingVO>> beforePractice(
            @AuthenticationPrincipal Long userId,
            @RequestParam @NotNull(message = "請選擇難易度！") WordleDifficulty difficulty) {
        List<WordleOngoingVO> games = wordleService.getOngoingGames(userId, WordleMode.PRACTICE, difficulty, null);
        return Result.success(games);
    }

}
