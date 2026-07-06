package com.ternura.model.dto;

import com.ternura.model.vo.HeatmapVO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProfileResponse {
    // 基本資料
    private Long id;
    private String username;
    private String avatar;
    private LocalDate createdAt;        // 加入時間

    // 追蹤關係
    private UserFollowResponse follow;

    // 徽章
    private Boolean isDailyDone;        // 今日謎題是否已完成

    // 統計數量
    private Integer totalDone;          // 所有已完成局數
    private Integer totalAchievements;  // 擁有成就數量（暫未開發，先回傳 0）

    // 打卡熱力圖
    private List<HeatmapVO> heatmap;    // { date, count }[]
}
