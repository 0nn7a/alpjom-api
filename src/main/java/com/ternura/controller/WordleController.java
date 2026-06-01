package com.ternura.controller;

import com.ternura.model.common.Result;
import com.ternura.model.dto.*;
import com.ternura.service.WordleService;
import com.ternura.utils.CurrentHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wordle")
@RequiredArgsConstructor
public class WordleController {
    private final WordleService wordleService;

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
}
