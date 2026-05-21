package com.ternura.exception;

import lombok.Getter;


@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object data;

    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_ERROR;
        this.data = null;
    }
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.data = null;
    }
    public BusinessException(ErrorCode errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }
}
