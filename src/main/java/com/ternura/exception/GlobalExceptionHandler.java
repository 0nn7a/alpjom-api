package com.ternura.exception;

import com.ternura.model.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 全局異常處理器
    @ExceptionHandler
    public ResponseEntity<Result> handleException(Exception e) {
        log.error(e.getMessage(), e);
        Result result = Result.error(ErrorCode.SYSTEM_ERROR);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    // 資料庫中的唯一值欄位發生重複
    @ExceptionHandler
    public ResponseEntity<Result> handleDuplicateKeyException(DuplicateKeyException e) {
        String msg = e.getMessage();
        log.error(msg, e);

        int idx = msg.indexOf("Duplicate entry");
        String errMsg = msg.substring(idx);
        String[] msgArr = errMsg.split(" ");
        Result result = Result.error(ErrorCode.DUPLICATE_KEY,msgArr[2] + " 已存在！");
        return ResponseEntity.status(result.getCode()).body(result);
    }

    // 業務邏輯異常
    @ExceptionHandler
    public ResponseEntity<Result> handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        Result result = Result.error(e.getErrorCode(), e.getMessage(), e.getData());
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
