package com.autoMotiveMes.dto.auth;

import lombok.Data;

/**
 * 实现功能【返回用户信息出参】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:48:29
 */
@Data
public class UserInfoResponseDto {
    private String realName;
    private String email;
    private String phone;
}
