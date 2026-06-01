package com.ternura.exception;

import com.ternura.model.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        log.error(e.getMessage(), e);
        String customMsg = "資料重複！";

        // 挖到最底層的 SQLIntegrityConstraintViolationException
        Throwable cause = e.getCause();
        while (cause != null && cause.getCause() instanceof SQLIntegrityConstraintViolationException) {
            cause = cause.getCause();
        }

        if (cause != null) {
            Matcher matcher = Pattern.compile("Duplicate entry '(.+)' for key '(.+)'")
                    .matcher(cause.getMessage());
            if (matcher.find()) {
                String value = matcher.group(1);
                String keyName = matcher.group(2);
                String field = keyName.contains(".")
                        ? keyName.split("\\.")[1]
                        : keyName;
                customMsg = "'" + value + "' 在 " + field + " 欄位已存在！";
            }
        }

        Result result = Result.error(ErrorCode.DUPLICATE_KEY, customMsg);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    // 將 Body 請求參數反序列化成指定類型時遇到的格式或類型解析異常
    // 或 Body 請求為空、傳遞的 JSON 格式有誤、與預期型別不符等
    @ExceptionHandler
    public ResponseEntity<Result> handleMalformedBodyException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);

        String customMsg = "參數格式解析異常！";
        Throwable cause = e.getCause();

        if (cause instanceof InvalidFormatException ife && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            // 枚舉值不合法的情況
            String inputValue = String.valueOf(ife.getValue());
            String enumClass = ife.getTargetType().getSimpleName();
            String validValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            customMsg = "'" + inputValue + "' 不是合法的 " + enumClass + " 選項，可用值為：" + validValues;
        } else if (cause instanceof MismatchedInputException mie) {
            // 與預期型別不符
            String fieldName = mie.getPath().isEmpty() ? "未知欄位" : mie.getPath().getFirst().getPropertyName();
            String expectedType = mie.getTargetType() != null ? mie.getTargetType().getSimpleName() : "未知型別";
            customMsg = "欄位 '" + fieldName + "' 型別不符，預期為：" + expectedType;
        }

        Result result = Result.error(ErrorCode.INVALID_INPUT, customMsg);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    // Body 請求參數的自定義驗證未過關，如規定必填卻未傳
    @ExceptionHandler
    public ResponseEntity<Result> handleInvalidBodyArgsException(MethodArgumentNotValidException e) {
        String customMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("\n"));

        if (customMsg.isEmpty()) {
            customMsg = "參數不合法，未通過驗證！";
        }

        Result result = Result.error(ErrorCode.INVALID_INPUT, customMsg);
        return ResponseEntity.status(result.getCode()).body(result);

        // 如果要改成多屬性的寫法：
        // Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
        //         .collect(Collectors.toMap(
        //                 FieldError::getField,
        //                 FieldError::getDefaultMessage,
        //                 (existing, replacement) -> existing  // 同欄位多個錯誤只取第一個
        //         ));
        //
        // Result result = Result.error(ErrorCode.INVALID_INPUT, "參數驗證失敗！", errors);
        // {
        //   "code": 422,
        //   "message": "參數驗證失敗！",
        //   "data": {
        //     "username": "用戶名稱不能為空",
        //     "email": "Email 格式不正確"
        //   }
        // }
    }

    // Controller method 參數有傳欄位但驗證失敗（RequestParam / PathVariable / Header 等）
    @ExceptionHandler
    public ResponseEntity<Result> handleInvalidMethodArgsException(HandlerMethodValidationException e) {
        log.error(e.getMessage(), e);

        // 收集所有驗證失敗的參數名稱與訊息
        StringBuilder details = new StringBuilder();
        boolean[] isTokenRelated = {false}; // 標記這次失敗是否與 token 相關，決定後續要用 401 還是 422
        // 內部匿名類別（new Visitor() { ... }）只能讀取外部的 final 或 effectively final 變數
        // 沒辦法直接修改一個普通的 boolean，但透過陣列可以繞過這個限制，修改 isTokenRelated[0] 的值

        // Spring 官方提供了 visitResults() 搭配 Visitor 的方式
        // 可以針對 requestHeader、requestParam 等不同來源各自處理
        e.visitResults(new HandlerMethodValidationException.Visitor() {
            @Override
            public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
                String name = requestHeader.value().isEmpty()
                        ? requestHeader.name()
                        : requestHeader.value();

                // 判斷是否為身分驗證相關
                if ("X-Refresh-Token".equalsIgnoreCase(name)) {
                    isTokenRelated[0] = true;
                }
                result.getResolvableErrors().forEach(err ->
                        details.append(name).append(": ").append(err.getDefaultMessage()).append(";")
                );
            }

            @Override
            public void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result) {
                String name = requestParam != null && !requestParam.value().isEmpty()
                        ? requestParam.value()
                        : result.getMethodParameter().getParameterName();
                result.getResolvableErrors().forEach(err ->
                        details.append(name).append(": ").append(err.getDefaultMessage()).append(";")
                );
            }

            @Override
            public void other(ParameterValidationResult result) {
                // 其他來源的 fallthrough
                String name = result.getMethodParameter().getParameterName();
                result.getResolvableErrors().forEach(err ->
                        details.append(name != null ? name : "參數")
                                .append(": ").append(err.getDefaultMessage()).append(";")
                );
            }

            // 以下目前專案用不到，留空即可
            @Override public void cookieValue(@NonNull CookieValue c, @NonNull ParameterValidationResult r) {}
            @Override public void matrixVariable(@NonNull MatrixVariable m, @NonNull ParameterValidationResult r) {}
            @Override public void modelAttribute(@Nullable ModelAttribute m, @NonNull ParameterErrors e) {}
            @Override public void pathVariable(@NonNull PathVariable p, @NonNull ParameterValidationResult r) {}
            @Override public void requestBody(@NonNull RequestBody rb, @NonNull ParameterErrors e) {}
            @Override public void requestPart(@NonNull RequestPart rp, @NonNull ParameterErrors e) {}
        });

        String message = details.isEmpty() ? "參數驗證失敗" : details.substring(0, details.length() - 1); // 去掉末尾的「;」
        ErrorCode code = isTokenRelated[0] ? ErrorCode.UNAUTHORIZED : ErrorCode.INVALID_INPUT;
        Result result = Result.error(code, message);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    // 請求 header 中所需欄位不存在
    @ExceptionHandler
    public ResponseEntity<Result> handleMissingHeaderException(MissingRequestHeaderException e) {
        log.error(e.getMessage(), e);
        String headerName = e.getHeaderName();

        // 其他欄位的 header 缺失 -> 400
        ErrorCode code = ErrorCode.INVALID_INPUT;

        // 身分驗證相關的 header 缺失 -> 401
        if ("X-Refresh-Token".equalsIgnoreCase(headerName)) {
            code = ErrorCode.UNAUTHORIZED;
        }

        Result result = Result.error(code, "請求 Header 缺少必要欄位：" + headerName);
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
