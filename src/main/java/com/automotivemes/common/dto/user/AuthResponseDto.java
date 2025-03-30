package com.automotivemes.common.dto.user;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private String[] roles;
    private String[] permissions;
}