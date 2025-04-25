package com.autoMotiveMes.common.exception;

import com.autoMotiveMes.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.rmi.ServerException;

/**
 * 实现功能【全局异常捕获处理类】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:36:58
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务异常（HTTP 400）
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return R.fail(e.getErrorCode(), e.getMessage());
    }

    // 处理认证异常（HTTP 401）
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public R<?> handleAuthException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return R.error(HttpStatus.UNAUTHORIZED.value(), "请重新登录");
    }

    // 处理权限异常（HTTP 403）
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public R<?> handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return R.error(HttpStatus.FORBIDDEN.value(), "无权访问该资源");
    }

    // 处理服务器异常（HTTP 500）
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServerException.class)
    public R<?> handleServerException(ServerException e) {
        log.error("服务器异常: {}", e.getMessage());
        return R.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误");
    }
}