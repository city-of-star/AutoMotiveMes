package com.automotivemes.common.exception;

import com.automotivemes.common.response.CommonResponse;
import com.automotivemes.common.response.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<CommonResponse<Object>> handleAuthException(AuthException e) {
        return ResponseUtils.badRequest(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<Object>> handleRuntimeException(RuntimeException e) {
        return ResponseUtils.internalServerError("系统内部错误：" + e.getMessage());
    }
}