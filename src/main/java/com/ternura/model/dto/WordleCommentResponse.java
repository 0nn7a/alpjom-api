package com.ternura.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WordleCommentResponse {
    private Long id;
    private String username;
    private String avatar;
    private String content;
    private LocalDateTime createdAt;
}
