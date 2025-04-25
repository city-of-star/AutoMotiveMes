package com.autoMotiveMes.common.response;

import lombok.Getter;

/**
 * 实现功能【业务错误码枚举】
 *
 * @author li.hongyu
 * @date 2025-04-25 11:28:13
 */
@Getter
public enum ErrorCode {
    // 认证相关
    UNAUTHORIZED(1, "登录凭证已过期"),
    INVALID_CREDENTIALS(2, "用户名或密码错误"),
    ACCOUNT_DISABLED(3, "账号已停用"),
    ACCOUNT_LOCK(4, "账号已锁定"),


    // 注册相关
    USERNAME_EXISTS(10, "用户名已存在"),
    EMAIL_EXISTS(11, "邮箱已被注册"),
    phone_EXISTS(11, "手机号码已存在"),

    // 业务操作
    INVALID_OPERATION(20, "非法操作"),

    // 系统保留
    UNKNOWN_ERROR(999, "系统繁忙");

    private final int code;
    private final String defaultMsg;

    ErrorCode(int code, String defaultMsg) {
        this.code = code;
        this.defaultMsg = defaultMsg;
    }

}