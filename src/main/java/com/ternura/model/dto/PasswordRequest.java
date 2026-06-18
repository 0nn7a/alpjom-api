package com.ternura.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordRequest {
    @NotBlank(message = "舊密碼不得為空！")
    private String passwordOld;

    @NotBlank(message = "新密碼不得為空！")
    private String password;
}
