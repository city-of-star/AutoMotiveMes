package com.autoMotiveMes.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 实现功能【通用工具类】
 *
 * @author li.hongyu
 * @date 2025-04-08 23:10:09
 */
public class CommonUtils {

    // redis 存入的设备实时参数数据的公共key
    public static final String REDIS_KEY_PREFIX = "equipment:";
    // redis 存入的设备实时参数数据的过期时间
    public static final int DATA_EXPIRE_MINUTES = 5;

    // 返回当前用户的用户名
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 检查是否存在有效认证且非匿名用户
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }
}