package com.autoMotiveMes.dto.system;

import lombok.Data;

/**
 * 实现功能【获取用户信息出参】
 *
 * @author li.hongyu
 * @date 2025-04-13 21:14:53
 */
@Data
public class GetUserInfoVo {
    private Long userId;
    private String username;
    private String realName;
    private String headImg;
    private String phone;
    private String email;
    private Long deptId;
    private Long postId;
    private Integer status;
    private Integer roleId;
}