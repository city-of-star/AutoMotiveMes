package com.autoMotiveMes.common.exception;

/**
 * 实现功能【自定义鉴权失败异常】
 *
 * @author li.hongyu
 * @date 2025-04-07 19:58:00
 */
public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message) { super(message); }
}