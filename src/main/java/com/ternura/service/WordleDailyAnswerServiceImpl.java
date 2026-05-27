package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ternura.mapper.WordleDailyAnswerMapper;
import com.ternura.mapper.WordleWordMapper;
import com.ternura.model.entity.WordleDailyAnswer;
import com.ternura.model.entity.WordleWord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class WordleDailyAnswerServiceImpl
        extends ServiceImpl<WordleDailyAnswerMapper, WordleDailyAnswer>
        implements WordleDailyAnswerService {
    private final WordleWordMapper wordleWordMapper;
    private final WordleDailyAnswerMapper wordleDailyAnswerMapper;

    // 如果同一瞬間兩個用戶都是「今天第一個玩的人」，沒有 synchronized 的話可能會同時通過 puzzle == null 的判斷，建立出兩筆同一天的謎題
    // synchronized 能確保 selectTodayAnswer 同一時間只有一個執行緒能進去執行
    @Override
    public synchronized WordleDailyAnswer selectTodayAnswer() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        // 檢查今日謎題是否已建立
        WordleDailyAnswer answer = wordleDailyAnswerMapper.selectTodayAnswer(today);

        // 沒有的話才建立
        if (answer == null) {
            WordleWord word = wordleWordMapper.selectAnswerByRandom();
            answer = new WordleDailyAnswer();
            answer.setWordId(word.getId());
            answer.setDate(today);
            save(answer);
        }

        return answer;
    }
}
