package com.ternura.service;

import com.ternura.model.dto.PageRequest;
import com.ternura.model.dto.PageResponse;
import com.ternura.model.entity.WordleRecord;

public interface GameService {
    long countFinished();

    PageResponse<WordleRecord> recordFinished(String username, PageRequest request);
}
