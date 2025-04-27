package com.autoMotiveMes.common.exception;

import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

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
        if (e.getMessage() == null) {
            // 处理业务异常消息
            log.warn("业务异常: 【{}】", e.getErrorCode().getMsg());
            return R.fail(e.getErrorCode().getCode(), e.getErrorCode().getMsg());
        } else {
            // 处理自定义业务异常消息
            log.warn("业务异常: 【{}】", e.getMessage());
            return R.fail(e.getErrorCode().getCode(), e.getMessage());
        }
    }

    // 处理服务器异常（HTTP 500）
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServerException.class)
    public R<?> handleServerException(ServerException e) {
        log.error("服务器异常: 【{}】", e.getMessage());
        return R.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.SYSTEM_ERROR.getMsg());
    }
}