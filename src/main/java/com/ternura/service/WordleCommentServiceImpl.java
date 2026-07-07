package com.ternura.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.mapper.WordleCommentMapper;
import com.ternura.mapper.WordleRecordMapper;
import com.ternura.model.dto.WordleCommentRequest;
import com.ternura.model.entity.WordleComment;
import com.ternura.model.entity.WordleRecord;
import com.ternura.model.vo.WordleCommentVO;
import com.ternura.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordleCommentServiceImpl extends ServiceImpl<WordleCommentMapper, WordleComment> implements WordleCommentService {
    private final WordleCommentMapper wordleCommentMapper;
    private final WordleRecordMapper wordleRecordMapper;

    @Override
    public void insert(Long userId, WordleCommentRequest request) {
        // 根據 shareToken 查找遊戲資料
        WordleRecord record = wordleRecordMapper.selectByShareToken(request.getShareToken());
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到符合遊戲！");
        }

        // 插入一筆新留言
        WordleComment comment = new WordleComment();
        comment.setRecordId(record.getId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setCreatedAt(TimeUtils.now());
        comment.setUpdatedAt(comment.getCreatedAt());
        comment.setIsDeleted(false);

        save(comment);
    }

    @Override
    public void delete(Long userId, Long id) {
        // 根據 userId 與 id 查找是否存在且為本人留言
        WordleComment comment = wordleCommentMapper.selectById(id);

        if (comment == null || comment.getIsDeleted()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "該留言不存在！");
        }

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "無權限刪除此留言！");
        }

        int rows = wordleCommentMapper.softDeleteById(id, TimeUtils.now());
        if (rows == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "刪除失敗，請稍後再試！");
        }
    }

    @Override
    public List<WordleCommentVO> selectByRecordId(Long recordId) {
        return wordleCommentMapper.selectByRecordId(recordId);
    }
}
