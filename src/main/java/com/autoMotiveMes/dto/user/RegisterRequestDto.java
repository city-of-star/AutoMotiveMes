package com.autoMotiveMes.dto.user;

import lombok.Data;

/**
 * 实现功能【注册请求入参】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:46:27
 */
@Data
public class RegisterRequestDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}