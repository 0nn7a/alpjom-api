package com.ternura.model.vo;

import lombok.Data;

@Data
public class WordleLikeVO {
    private long count;
    private boolean byMe; // 當前登入者是否已按讚
}
