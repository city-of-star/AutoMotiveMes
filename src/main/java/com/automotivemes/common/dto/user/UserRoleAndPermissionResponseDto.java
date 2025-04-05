package com.automotivemes.common.dto.user;

import lombok.Data;

/**
 * 实现功能【返回用户角色权限出参】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:50:48
 */
@Data
public class UserRoleAndPermissionResponseDto {
    private String[] roles;
    private String[] permissions;
}
