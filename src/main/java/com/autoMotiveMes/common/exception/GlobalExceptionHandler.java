package com.autoMotiveMes.common.exception;

import com.autoMotiveMes.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public R<?> handleBadRequestException(BadRequestException e) {
        log.warn("错误请求响应：{ {} }", e.getMessage());
        return R.badRequest(e.getMessage());
    }

    // 401
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthException.class)
    public R<?> handleAuthException(AuthException e) {
        log.warn("登录验证失败：{ {} }", e.getMessage());
        return R.unauthorized(e.getMessage());
    }

    // 403
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public R<?> handleForbiddenException(ForbiddenException e) {
        log.warn("权限验证失败：{ {} }", e.getMessage());
        return R.forbidden(e.getMessage());
    }

    // 500
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(GlobalException.class)
    public R<?> handleGlobalException(GlobalException e) {
        log.error("系统内部错误：{ {} }", e.getMessage());
        return R.serverError(e.getMessage());
    }

}