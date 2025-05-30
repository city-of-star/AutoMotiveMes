package com.autoMotiveMes.dto.system;

import lombok.Data;

/**
 * 实现功能【修改用户入参】
 *
 * @author li.hongyu
 * @date 2025-04-08 20:04:05
 */
@Data
public class UpdateUserDto {
    private Long userId;
    private String realName;
    private Integer roleId;
    private String phone;
    private String email;
    private Long deptId;
    private Long postId;
    private Integer status;
}