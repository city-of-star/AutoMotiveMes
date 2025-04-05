package com.automotivemes.common.exception;

/**
 * 实现功能【自定义全局异常】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:28:39
 */
public class GlobalException extends RuntimeException {
    public GlobalException(String message) {
        super(message);
    }
}