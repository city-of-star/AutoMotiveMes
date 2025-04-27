package com.autoMotiveMes.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现功能【返回体封装类】
 *
 * @author li.hongyu
 * @date 2025-03-30 15:25:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> {
    // 成功状态码
    public static final int SUCCESS_CODE = 200;
    // 业务失败基础码
    public static final int BUSINESS_ERROR_BASE = 1000;

    private Integer code;
    private String msg;
    private T data;

    // 成功响应
    public static <T> R<T> success() {
        return new R<>(SUCCESS_CODE, "success", null);
    }

    // 成功响应（带数据）
    public static <T> R<T> success(T data) {
        return new R<>(SUCCESS_CODE, "success", data);
    }

    // 认证失败
    public static <T> R<T> unauthorized() {
        return new R<>(null, null, null);
    }

    // 无权访问
    public static <T> R<T> forbidden() {
        return new R<>(null, null, null);
    }

    // 业务失败
    public static <T> R<T> fail(int code, String message) {
        return new R<>(BUSINESS_ERROR_BASE + code, message, null);
    }

    // 系统级错误
    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null);
    }
}