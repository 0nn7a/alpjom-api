package com.ternura.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    // MyBatis-Plus 的註解，用來標記這個欄位是資料表的主鍵
    // IdType.AUTO: 由資料庫自動遞增（AUTO_INCREMENT）
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password; // Hashed
    private String salt;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
