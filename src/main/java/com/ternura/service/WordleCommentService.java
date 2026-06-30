package com.ternura.service;

import com.ternura.model.dto.WordleCommentRequest;
import com.ternura.model.vo.WordleCommentVO;

import java.util.List;

public interface WordleCommentService {
    void insert(Long userId, WordleCommentRequest request);

    void delete(Long userId, Long id);

    List<WordleCommentVO> selectByGameId(Long gameId);
}
