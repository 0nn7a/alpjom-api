package com.ternura.service;

import com.ternura.model.dto.PageRequest;
import com.ternura.model.dto.PageResponse;
import com.ternura.model.dto.WordleRecordResponse;

public interface GameService {
    long countFinished();

    PageResponse<WordleRecordResponse> recordFinished(String username, PageRequest request);
}
