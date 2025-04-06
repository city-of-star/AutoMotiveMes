package com.autoMotiveMes.service.auth;

import com.autoMotiveMes.common.dto.user.*;

/**
 * 实现功能【用户认证服务接口】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:46:31
 */
public interface AuthService {
    void register(RegisterRequestDto registerRequestDto);
    AuthResponseDto login(LoginRequestDto loginRequestDto);
    UserInfoResponseDto getUserInfo();
    UserRoleAndPermissionResponseDto getUserRoleAndPermission();
}