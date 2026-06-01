package com.ternura.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "username 不可為空！")
    private String username;

    @NotBlank(message = "email 不可為空！")
    private String email;

    @NotBlank(message = "password 不可為空！")
    private String password;
}
