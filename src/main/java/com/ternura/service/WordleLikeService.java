package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ternura.model.entity.WordleLike;
import com.ternura.model.dto.WordleLikeResponse;

public interface WordleLikeService extends IService<WordleLike> {
    WordleLikeResponse toggle(Long userId, String shareToken);
}
