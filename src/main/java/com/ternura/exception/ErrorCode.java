package com.ternura.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SYSTEM_ERROR(500, "未知錯誤，請聯繫系統管理員！"),
    DUPLICATE_KEY(409, "資料重複！"),
    NOT_FOUND(404, "未找到符合資料！"),
    UNAUTHORIZED(401, "身份驗證失敗！"),
    BUSINESS_ERROR(400, "業務邏輯錯誤！");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
