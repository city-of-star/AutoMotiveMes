package com.autoMotiveMes.common.dto.user;

import lombok.Data;

/**
 * 实现功能【登录请求入参】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:38:39
 */
@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
