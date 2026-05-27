package com.ternura.controller;

import com.ternura.model.common.Result;
import com.ternura.model.dto.WordleStartRequest;
import com.ternura.model.dto.WordleStartResponse;
import com.ternura.service.WordleService;
import com.ternura.utils.CurrentHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wordle")
@RequiredArgsConstructor
public class WordleController {
    private final WordleService wordleService;

    @PostMapping("/start")
    public Result<WordleStartResponse> start(@RequestBody WordleStartRequest request) {
        Long userId = CurrentHolder.getCurrentId();
        WordleStartResponse response = wordleService.start(userId, request);
        return Result.success(response);
    }
}
