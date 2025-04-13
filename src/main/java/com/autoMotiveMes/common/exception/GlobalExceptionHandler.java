package com.autoMotiveMes.common.exception;

import com.autoMotiveMes.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 实现功能【全局异常捕获处理类】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:36:58
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400
    @ExceptionHandler(BadRequestException.class)
    public R<?> handleBadRequestException(BadRequestException e) {
        log.warn("错误请求响应：{ {} }", e.getMessage());
        return R.badRequest(e.getMessage());
    }

    // 401
    @ExceptionHandler(AuthException.class)
    public R<?> handleAuthException(AuthException e) {
        log.warn("登录验证失败：{ {} }", e.getMessage());
        return R.unauthorized(e.getMessage());
    }

    // 403
    @ExceptionHandler(ForbiddenException.class)
    public R<?> handleForbiddenException(ForbiddenException e) {
        log.warn("权限验证失败：{ {} }", e.getMessage());
        return R.forbidden(e.getMessage());
    }
    // 500
    @ExceptionHandler(GlobalException.class)
    public R<?> handleGlobalException(GlobalException e) {
        log.error("系统内部错误：{ {} }", e.getMessage());
        return R.serverError(e.getMessage());
    }

}