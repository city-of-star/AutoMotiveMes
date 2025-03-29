package com.automotivemes.common.dto.user;

import lombok.Data;

@Data
public class UserRoleAndPermissionResponse {
    private String[] roles;
    private String[] permissions;
}
