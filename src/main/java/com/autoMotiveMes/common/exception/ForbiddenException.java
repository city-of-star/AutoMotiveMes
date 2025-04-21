package com.autoMotiveMes.common.exception;

import java.io.Serial;

/**
 * 实现功能【自定义鉴权失败异常】
 *
 * @author li.hongyu
 * @date 2025-04-07 19:58:00
 */
public class ForbiddenException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 3406054603674396745L;

    public ForbiddenException(String message) { super(message); }
}