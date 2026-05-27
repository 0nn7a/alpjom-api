package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ternura.model.entity.WordleDailyAnswer;

public interface WordleDailyAnswerService extends IService<WordleDailyAnswer> {
    WordleDailyAnswer selectTodayAnswer();
}
