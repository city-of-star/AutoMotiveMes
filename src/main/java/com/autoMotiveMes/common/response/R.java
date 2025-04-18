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
    private int code;
    private String msg;
    private T data;

    // 200 有数据的成功响应
    public static <T> R<T> success(T data) {
        return new R<>(200, "success", data);
    }

    // 200 无数据的成功响应
    public static <T> R<T> successWithoutData() {
        return new R<>(200, "success", null);
    }

    // 400 错误请求响应
    public static <T> R<T> badRequest(String message) { return new R<>(400, message, null); }

    // 401 未授权响应
    public static <T> R<T> unauthorized(String message) {
        return new R<>(401, message, null);
    }

    // 403 禁止访问响应
    public static <T> R<T> forbidden(String message) { return new R<>(403, message, null); }

    // 500 服务器内部错误响应
    public static <T> R<T> serverError(String message) { return new R<>(500, message, null); }
}