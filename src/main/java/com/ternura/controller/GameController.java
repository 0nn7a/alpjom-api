package com.ternura.controller;

import com.ternura.model.common.Result;
import com.ternura.model.dto.PageRequest;
import com.ternura.model.dto.PageResponse;
import com.ternura.model.entity.WordleRecord;
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


    @GetMapping("/record/finished/{username}")
    public Result<PageResponse<WordleRecord>> recordFinished(@PathVariable String username, PageRequest request){
        PageResponse<WordleRecord> records = gameService.recordFinished(username, request);
        return Result.success(records);
    }
}
