package com.autoMotiveMes.common.exception;

import java.io.Serial;

/**
 * 实现功能【自定义权限验证异常】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:23:34
 */
public class AuthException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2828370446023896259L;

    public AuthException(String message) { super(message); }
}
