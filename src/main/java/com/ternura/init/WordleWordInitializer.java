package com.ternura.init;

import com.ternura.mapper.WordleWordMapper;
import com.ternura.model.entity.WordleWord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordleWordInitializer implements ApplicationRunner {
    private final WordleWordMapper wordleWordMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (wordleWordMapper.selectCount(null) > 0) {
            log.info("單詞庫已存在，跳過初始化匯入！");
            return;
        }

        log.info("=== 單詞庫開始進行初始化匯入 ===");
        importWords("/wordle/answers.txt", true);  // 先匯入答案候選單詞
        importWords("/wordle/guesses.txt", false); // 再匯入允許猜測單詞
        log.info("=== 單詞庫初始化匯入完成，共 {} 筆 ===", wordleWordMapper.selectCount(null));
    }

    private void importWords(String path, boolean isAnswerCandidate) throws Exception {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            log.warn("找不到檔案: {}", path);
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        List<WordleWord> batch = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String w = line.trim().toLowerCase();
            if (w.isEmpty()) continue;

            WordleWord word = new WordleWord();
            word.setWord(w);
            word.setLength(w.length());
            word.setIsAnswerCandidate(isAnswerCandidate);
            batch.add(word);

            // 每 500 筆批次插入
            if (batch.size() >= 500) {
                wordleWordMapper.insertBatch(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            wordleWordMapper.insertBatch(batch);
        }

        log.info("{} 檔案已成功匯入！", path);
    }
}
