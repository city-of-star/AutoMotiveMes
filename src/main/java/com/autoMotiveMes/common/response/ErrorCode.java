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
    // 认证相关(401)
    LOGIN_INFO_EXPIRED(1, "登录信息已过期，请重新登录"),
    ERROR_AUTHENTICATION_HEADER(2, "无效的认证头"),
    USER_NOT_EXISTS(3, "用户不存在"),
    ERROR_TOKEN(4, "无效的token"),

    // 权限相关(403)
    NOT_PERMISSION(10, "没有操作权限"),
    ACCOUNT_DISABLED(11, "账号已停用"),
    ACCOUNT_LOCKED(12, "账号已锁定"),

    // 登录与注册业务相关(400)
    ERROR_USERNAME_OR_PASSWORD(20, "用户名或密码错误"),
    USERNAME_EXISTS(21, "用户名已存在"),
    EMAIL_EXISTS(22, "邮箱已被注册"),
    PHONE_EXISTS(23, "手机号码已存在"),
    PASSWORDS_DIFFER(24, "两次输入的密码不一致"),

    // 系统管理业务相关
    ROLE_IS_USE(30, "角色存在关联用户，无法删除"),
    ROLE_NAME_EXISTS(31, "角色名称已存在"),
    ROLE_CODE_EXISTS(32, "角色编码已存在"),

    // 其他业务相关(400)
    INVALID_OPERATION(100, "非法操作"),
    INVALID_OPERATION1(101, "指定产品不存在"),
    INVALID_OPERATION2(102, "计划开始时间不能晚于结束时间"),
    INVALID_OPERATION3(103, "工单状态不合法或不存在"),
    INVALID_OPERATION4(104, "工序[?]无可用设备"),
    INVALID_OPERATION5(105, "工单不存在"),
    INVALID_OPERATION6(106, "当前状态不允许删除"),
    INVALID_OPERATION7(107, "存在此工单的排程计划，无法删除"),
    INVALID_OPERATION8(108, ""),


    // 系统内部错误
    SYSTEM_ERROR(90, "系统内部错误"),

    // 系统保留
    UNKNOWN_ERROR(99, "系统繁忙");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}