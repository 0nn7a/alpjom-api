package com.ternura.service;

import com.ternura.model.dto.WordleCommentRequest;
import com.ternura.model.dto.WordleCommentResponse;

import java.util.List;

public interface WordleCommentService {
    void insert(Long userId, WordleCommentRequest request);

    void delete(Long userId, Long id);

    List<WordleCommentResponse> selectByRecordId(Long recordId);
}
