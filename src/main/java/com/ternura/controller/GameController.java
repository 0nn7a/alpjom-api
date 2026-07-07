package com.ternura.controller;

import com.ternura.model.common.Result;
import com.ternura.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/count/finished")
    public Result<Long> countFinished(){
        Long count = gameService.countFinished();
        return Result.success(count);
    }
}
