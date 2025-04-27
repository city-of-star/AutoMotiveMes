package com.autoMotiveMes.common.exception;

import com.autoMotiveMes.common.response.ErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 实现功能【自定义业务异常】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:27:23
 */
@Getter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 9196224339724626039L;

    private final ErrorCode errorCode;
    private String message = null;

    public BusinessException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public BusinessException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
