package com.autoMotiveMes.controller.auth;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.auth.*;
import com.autoMotiveMes.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【用户认证服务controller】
 *
 * @author li.hongyu
 * @date 2025-02-15 17:04:34
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public R<AuthVo> login(@RequestBody LoginDto loginDto) {
        AuthVo authVo = authService.login(loginDto);
        return R.success(authVo);
    }

    @PostMapping("/info")
    public R<UserInfoVo> info() {
        UserInfoVo userInfoVo = authService.getUserInfo();
        return R.success(userInfoVo);
    }

    @PostMapping("/getRoleAndPermission")
    public R<UserRoleAndPermissionVo> getRoleAndPermission() {
        UserRoleAndPermissionVo userRoleAndPermissionVo = authService.getUserRoleAndPermission();
        return R.success(userRoleAndPermissionVo);
    }

    @PostMapping("/isValidToken")
    public R<?> isValidToken(HttpServletRequest request) {
        authService.isValidToken(request);
        return R.success();
    }
}
