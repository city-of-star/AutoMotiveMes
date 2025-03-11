package com.automotivemes.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// 安全工具类
public class SecurityUtils {
    // 获取当前登录用户
    public static String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }

    // 获取客户端IP
    public static String getClientIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)attributes).getRequest();
            return request.getRemoteAddr();
        }
        return "0.0.0.0";
    }

    // 用户名脱敏（示例：隐藏部分字符）
    public static String maskUsername(String username) {
        if (username == null) return "unknown";
        if (username.length() <= 2) return username;
        return username.charAt(0) + "***" + username.charAt(username.length()-1);
    }
}
