package com.autoMotiveMes.controller.auth;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.system.*;
import com.autoMotiveMes.service.auth.AuthService;
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

    @PostMapping("/register")
    public R<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        authService.register(registerRequestDto);
        return R.successWithoutData();
    }

    @PostMapping("/login")
    public R<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        AuthResponseDto authResponseDto = authService.login(loginRequestDto);
        return R.success(authResponseDto);
    }

    @PostMapping("/info")
    public R<UserInfoResponseDto> info() {
        UserInfoResponseDto userInfoResponseDto = authService.getUserInfo();
        return R.success(userInfoResponseDto);
    }

    @PostMapping("/getRoleAndPermission")
    public R<UserRoleAndPermissionResponseDto> getRoleAndPermission() {
        UserRoleAndPermissionResponseDto userRoleAndPermissionResponseDto = authService.getUserRoleAndPermission();
        return R.success(userRoleAndPermissionResponseDto);
    }
}
