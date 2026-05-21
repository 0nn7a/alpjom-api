package com.ternura.model.common;

import com.ternura.exception.ErrorCode;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        return result;
    }
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        Result<T> result = new Result<>();
        result.code = errorCode.getCode();
        result.message = errorCode.getMessage();
        return result;
    }
    public static <T> Result<T> error(ErrorCode errorCode, String customMsg) {
        Result<T> result = new Result<>();
        result.code = errorCode.getCode();
        result.message = customMsg;
        return result;
    }
    public static <T> Result<T> error(ErrorCode errorCode, String customMsg, T data) {
        Result<T> result = new Result<>();
        result.code = errorCode.getCode();
        result.message = customMsg;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(int code, String customMsg) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = customMsg;
        return result;
    }
    public static <T> Result<T> error(int code, String customMsg, T data) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = customMsg;
        result.data = data;
        return result;
    }
}
