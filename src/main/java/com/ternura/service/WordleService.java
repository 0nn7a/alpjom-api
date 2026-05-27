package com.ternura.service;

import com.ternura.model.dto.WordleStartRequest;
import com.ternura.model.dto.WordleStartResponse;

public interface WordleService{
    WordleStartResponse start(Long userId, WordleStartRequest request);
}
