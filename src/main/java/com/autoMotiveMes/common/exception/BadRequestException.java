package com.autoMotiveMes.common.exception;

import java.io.Serial;

/**
 * 实现功能【自定义错误请求异常】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:27:23
 */
public class BadRequestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6943079042475845094L;

    public BadRequestException(String message) { super(message); }
}
