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
    public static final String DEFAULT_PASSWORD = "123456";
    // 默认用户头像
    public static final String HEAD_IMG_URL = "https://img0.baidu.com/it/u=3440970623,2745306240&fm=253&fmt=auto&app=138&f=JPEG?w=260&h=260";
    // 默认主题颜色
    public static final String DEFAULT_THEME_COLOR = "#409EFF";
}