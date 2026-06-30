package com.ternura.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WordleCommentRequest {
    @NotBlank(message = "shareToken 不可為空！")
    private String shareToken;

    @NotBlank(message = "請輸入留言內容！")
    @Size(max = 500, message = "留言不可超過 500 字！")
    private String content;
}
