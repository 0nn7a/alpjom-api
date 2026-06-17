package com.ternura.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AvatarDeleteRequest {
    @NotEmpty(message = "請選擇要刪除的頭貼！")
    private List<Long> ids;
}
