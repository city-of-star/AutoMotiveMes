package com.automotivemes.common.exception;

/**
 * 实现功能【自定义错误请求异常】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:27:23
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
