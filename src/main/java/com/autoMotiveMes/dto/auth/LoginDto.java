package com.autoMotiveMes.dto.auth;

import lombok.Data;

/**
 * 实现功能【登录请求入参】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:38:39
 */
@Data
public class LoginDto {
    private String username;
    private String password;
}
