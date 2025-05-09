package com.autoMotiveMes.dto.auth;

import lombok.Data;

import java.util.Date;

/**
 * 实现功能【返回用户信息出参】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:48:29
 */
@Data
public class UserInfoVo {
    private Long userId;
    private String username;
    private String realName;
    private Integer sex;
    private String themeColor;
    private String headImg;
    private String email;
    private String phone;
    private String roleName;
    private String deptName;
    private String postName;
    private Date createTime;
}
