package com.automotivemes.common.exception;

import com.automotivemes.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 500
    @ExceptionHandler(GlobalException.class)
    public R handleGlobalException(GlobalException e) {
        log.error("---------- 系统内部错误：{{}} ----------", e.getMessage());
        return R.serverError(e.getMessage());
    }

    // 401
    @ExceptionHandler(BadRequestException.class)
    public R handleBadRequestException(AuthException e) {
        log.error("---------- 错误请求响应：{{}} ----------", e.getMessage());
        return R.badRequest(e.getMessage());
    }

    // 401
    @ExceptionHandler(AuthException.class)
    public R handleAuthException(AuthException e) {
        log.error("---------- 权限验证失败：{{}} ----------", e.getMessage());
        return R.unauthorized(e.getMessage());
    }

}