package com.ternura.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WordleCommentVO {
    private Long id;
    private String username;
    private String avatar;
    private String content;
    private LocalDateTime createdAt;
}
