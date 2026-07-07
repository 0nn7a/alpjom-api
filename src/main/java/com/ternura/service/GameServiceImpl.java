package com.ternura.service;

import com.ternura.mapper.WordleRecordMapper;
import com.ternura.model.enums.WordleIsWin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final WordleRecordMapper wordleRecordMapper;

    @Override
    public long countFinished() {
        // wordle
        return wordleRecordMapper.countFinished();
    }
}
