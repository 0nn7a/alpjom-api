package com.ternura.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.mapper.UserMapper;
import com.ternura.mapper.WordleRecordMapper;
import com.ternura.model.dto.PageRequest;
import com.ternura.model.dto.PageResponse;
import com.ternura.model.entity.User;
import com.ternura.model.entity.WordleRecord;
import com.ternura.model.enums.WordleIsWin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final WordleRecordMapper wordleRecordMapper;
    private final UserMapper userMapper;

    @Override
    public long countFinished() {
        // wordle
        return wordleRecordMapper.countFinished();
    }

    @Override
    public PageResponse<WordleRecord> recordFinished(String username, PageRequest request) {
        // 用戶基本資料
        User user = userMapper.selectByUsername(username);
        if (user == null) throw new BusinessException(ErrorCode.NOT_FOUND, "用戶不存在！");

        PageHelper.startPage(request.getPage(), request.getSize());
        PageHelper.orderBy("finished_at DESC");

        // wordle records
        List<WordleRecord> list = wordleRecordMapper.selectByCondition(user.getId(), null, null, WordleIsWin.FINISHED, null);

        Page<WordleRecord> p = (Page<WordleRecord>) list;
        return new PageResponse<>(p.getTotal(), p.getResult());
    }
}
