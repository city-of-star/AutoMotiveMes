package com.automotivemes.common.dto.user;

import lombok.Data;

@Data
public class UserRoleAndPermissionResponseDto {
    private String[] roles;
    private String[] permissions;
}
