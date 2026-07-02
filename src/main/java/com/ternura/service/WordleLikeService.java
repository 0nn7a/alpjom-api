package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ternura.model.entity.WordleLike;
import com.ternura.model.vo.WordleLikeVO;

public interface WordleLikeService extends IService<WordleLike> {
    WordleLikeVO toggle(Long userId, String shareToken);
}
