package com.autoMotiveMes.common.constant;

/**
 * 实现功能【通用常量】
 *
 * @author li.hongyu
 * @date 2025-04-28 14:45:49
 */
public class CommonConstant {

    // redis 存入的设备实时参数数据的公共key
    public static final String REDIS_KEY_PREFIX = "equipment:";
    // redis 存入的设备实时参数数据的过期时间
    public static final int DATA_EXPIRE_MINUTES = 5;
    // 默认用户密码
    public static final String defaultPassword = "123456";
}