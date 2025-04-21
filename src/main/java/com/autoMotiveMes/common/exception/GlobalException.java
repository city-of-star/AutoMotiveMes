package com.autoMotiveMes.common.exception;

import java.io.Serial;

/**
 * 实现功能【自定义全局异常】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:28:39
 */
public class GlobalException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6966178082446507390L;

    public GlobalException(String message) {
        super(message);
    }
}