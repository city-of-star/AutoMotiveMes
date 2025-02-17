package com.automotivemes.controller;

import com.automotivemes.common.exception.AuthException;
import com.automotivemes.common.response.CommonResponse;
import com.automotivemes.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<CommonResponse<Object>> handleAuthException(AuthException e) {
        return ResponseUtil.badRequest(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<Object>> handleRuntimeException(RuntimeException e) {
        return ResponseUtil.internalServerError("系统内部错误：" + e.getMessage());
    }
}