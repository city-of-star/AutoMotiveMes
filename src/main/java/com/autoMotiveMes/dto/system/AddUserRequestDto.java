package com.autoMotiveMes.dto.system;

import lombok.Data;

/**
 * 实现功能【增加用户入参】
 *
 * @author li.hongyu
 * @date 2025-04-08 20:03:39
 */
@Data
public class AddUserRequestDto {
    private String username;
    private String password;
    private String realName;
    private Integer roleId;
    private String phone;
    private String email;
    private Long deptId;
    private Long postId;
    private Integer status;
}