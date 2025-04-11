package com.autoMotiveMes.dto.auth;

import lombok.Data;

/**
 * 实现功能【认证成功 出参】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:39:39
 */
@Data
public class AuthResponseDto {
    private String token;
    private String[] roles;
    private String[] permissions;
}