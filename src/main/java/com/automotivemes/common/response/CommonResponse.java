package com.automotivemes.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    private int code;
    private String message;
    private T data;

    // 有数据的成功响应
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, "Success", data);
    }

    // 无数据的成功响应
    public static <T> CommonResponse<T> successWithoutData() {
        return new CommonResponse<>(200, "Success", null);
    }

    // 错误响应
    public static <T> CommonResponse<T> error(int code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}