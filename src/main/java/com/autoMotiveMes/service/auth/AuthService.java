package com.autoMotiveMes.service.auth;

import com.autoMotiveMes.dto.auth.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 实现功能【用户认证服务接口】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:46:31
 */
public interface AuthService {
    // 登录
    AuthResponseDto login(LoginRequestDto loginRequestDto);

    // 获取用户信息
    UserInfoResponseDto getUserInfo();

    // 获取用户角色与权限
    UserRoleAndPermissionResponseDto getUserRoleAndPermission();

    // 验证用户token是否过期
    void isValidToken(HttpServletRequest request);
}