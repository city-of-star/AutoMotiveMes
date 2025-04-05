package com.automotivemes.common.exception;

/**
 * 实现功能【自定义权限验证异常】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:23:34
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) { super(message); }
}
