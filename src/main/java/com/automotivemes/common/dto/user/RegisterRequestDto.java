package com.automotivemes.common.dto.user;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}