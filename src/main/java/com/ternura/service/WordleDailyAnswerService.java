package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ternura.model.entity.WordleDailyAnswer;

import java.time.LocalDate;

public interface WordleDailyAnswerService extends IService<WordleDailyAnswer> {
    WordleDailyAnswer selectAnswerByDate(LocalDate date);
}
