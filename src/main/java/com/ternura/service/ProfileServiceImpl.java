package com.ternura.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ternura.exception.BusinessException;
import com.ternura.exception.ErrorCode;
import com.ternura.mapper.UserAvatarMapper;
import com.ternura.mapper.UserMapper;
import com.ternura.mapper.WordleGameRecordMapper;
import com.ternura.model.dto.AvatarDeleteRequest;
import com.ternura.model.dto.ProfileRequest;
import com.ternura.model.dto.ProfileResponse;
import com.ternura.model.entity.User;
import com.ternura.model.entity.UserAvatar;
import com.ternura.model.vo.HeatmapVO;
import com.ternura.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserMapper userMapper;
    private final WordleGameRecordMapper wordleGameRecordMapper;
    private final UserAvatarMapper userAvatarMapper;
    private final CloudService cloudService;

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

    @Override
    public void updateProfile(Long userId, ProfileRequest request) {
        User user = userMapper.selectById(userId);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            // 確認新的 username 未被使用
            User existed = userMapper.selectByUsername(request.getUsername());
            if (existed != null && !existed.getId().equals(user.getId())) {
                throw new BusinessException(ErrorCode.DUPLICATE_KEY, "該 username 已被使用！");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getAvatarId() != null) {
            UserAvatar avatar = userAvatarMapper.selectByUserId(userId, request.getAvatarId());
            if (avatar == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "未找到符合頭貼！");
            }
            user.setAvatar(avatar.getFileUrl());
        }

        user.setUpdatedAt(TimeUtils.now());
        userMapper.updateById(user);
    }

    @Override
    public List<UserAvatar> getAvatar(Long userId) {
        return userAvatarMapper.selectList(new QueryWrapper<UserAvatar>().eq("user_id", userId));
    }

    @Override
    public UserAvatar uploadAvatar(Long userId, MultipartFile file) {
        // 1. 上傳圖片到 R2
        String fileUrl = cloudService.upload(file);

        // 2. 記錄到 user_avatar 表
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setUserId(userId);
        userAvatar.setFileUrl(fileUrl);
        userAvatarMapper.insert(userAvatar);

        return userAvatar;
    }

    @Override
    @Transactional
    public void deleteAvatar(Long userId, AvatarDeleteRequest request) {
        List<Long> ids = request.getIds();

        // 1. 查出對應記錄
        List<UserAvatar> avatars = userAvatarMapper.selectByUserIds(userId, ids);
        if (avatars.isEmpty()) return;

        // 2. 先取出 fileUrl 給 R2 刪除（失敗就直接拋例外，DB 不動）
        cloudService.deleteBatch(avatars.stream().map(UserAvatar::getFileUrl).toList());

        // 3. R2 成功後才刪 DB
        userAvatarMapper.deleteByUserIds(userId, ids);

        // 4. 檢查是否需要重置頭貼回預設值
        User user = userMapper.selectById(userId);
        boolean isCurrentAvatarDeleted = avatars.stream().anyMatch(a -> a.getFileUrl().equals(user.getAvatar()));
        if (isCurrentAvatarDeleted) {
            userMapper.resetAvatarToDefault(userId);
        }
    }
}
