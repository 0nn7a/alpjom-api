package com.ternura.service;

import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.mapper.UserMapper;
import com.ternura.mapper.WordleGameRecordMapper;
import com.ternura.model.dto.ProfileResponse;
import com.ternura.model.entity.User;
import com.ternura.model.vo.HeatmapVO;
import com.ternura.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserMapper userMapper;
    private final WordleGameRecordMapper wordleGameRecordMapper;

    @Override
    public ProfileResponse getProfile(String username) {
        // 1. 用戶基本資料
        User user = userMapper.selectByUsername(username);
        if (user == null) throw new BusinessException(ErrorCode.NOT_FOUND, "用戶不存在！");

        // 2. 今日謎題已完成徽章
        boolean isDailyDone = wordleGameRecordMapper.isDailyDoneByUserDate(user.getId(), TimeUtils.today());

        // 3. 所有已完成局數
        int totalDone = wordleGameRecordMapper.countTotalDoneByUser(user.getId());

        // 4. 打卡熱力圖
        List<HeatmapVO> heatmap = wordleGameRecordMapper.selectHeatmapByUser(user.getId());

        // 組裝資料回傳
        ProfileResponse response = new ProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setAvatar(user.getAvatar());
        response.setCreatedAt(user.getCreatedAt().toLocalDate());
        response.setIsDailyDone(isDailyDone);
        response.setTotalDone(totalDone);
        response.setTotalAchievements(0); // 暫未開發，先回傳 0
        response.setHeatmap(heatmap);

        return response;
    }
}
