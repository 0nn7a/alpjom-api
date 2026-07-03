package com.ternura.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.mapper.WordleRecordMapper;
import com.ternura.mapper.WordleLikeMapper;
import com.ternura.model.entity.WordleRecord;
import com.ternura.model.vo.WordleLikeVO;
import com.ternura.model.entity.WordleLike;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WordleLikeServiceImpl extends ServiceImpl<WordleLikeMapper, WordleLike> implements WordleLikeService {
    private final WordleLikeMapper wordleLikeMapper;
    private final WordleRecordMapper wordleRecordMapper;

    @Override
    public WordleLikeVO toggle(Long userId, String shareToken) {
        // 確認該遊戲是否存在
        WordleRecord record = wordleRecordMapper.selectByShareToken(shareToken);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到遊戲！");
        }

        // 查詢該用戶是否已經給此局遊戲按過讚
        WordleLike existed = getOne(new LambdaQueryWrapper<WordleLike>()
                .eq(WordleLike::getRecordId, record.getId())
                .eq(WordleLike::getUserId, userId));

        if (existed != null) {
            // 已按讚 -> 取消 -> 刪除該資料
            wordleLikeMapper.deleteById(existed.getId());
        } else {
            // 未按讚 -> 新增資料
            WordleLike wordleLike = new WordleLike();
            wordleLike.setRecordId(record.getId());
            wordleLike.setUserId(userId);
            save(wordleLike);
        }

        WordleLikeVO like = new WordleLikeVO();
        long likeCount = count(new LambdaQueryWrapper<WordleLike>().eq(WordleLike::getRecordId, record.getId()));
        boolean likedByMe = existed == null;
        like.setCount(likeCount);
        like.setByMe(likedByMe);
        return like;
    }
}
