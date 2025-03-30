package com.automotivemes.common.dto.user;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
