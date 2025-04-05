package com.automotivemes.controller.user;

import com.automotivemes.common.dto.user.*;
import com.automotivemes.common.response.R;
import com.automotivemes.entity.user.SysUser;
import com.automotivemes.service.user.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【用户认证服务controller】
 *
 * @author li.hongyu
 * @date 2025-02-15 17:04:34
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public R register(@RequestBody RegisterRequestDto registerRequestDto) {
        userService.register(registerRequestDto);
        return R.successWithoutData();
    }

    @PostMapping("/login")
    public R<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        AuthResponseDto authResponseDto = userService.login(loginRequestDto);
        return R.success(authResponseDto);
    }

    @PostMapping("/info")
    public R<UserInfoResponseDto> info() {
        UserInfoResponseDto userInfoResponseDto = userService.getUserInfo();
        return R.success(userInfoResponseDto);
    }

    @PostMapping("/getRoleAndPermission")
    public R<UserRoleAndPermissionResponseDto> getRoleAndPermission() {
        UserRoleAndPermissionResponseDto userRoleAndPermissionResponseDto = userService.getUserRoleAndPermission();
        return R.success(userRoleAndPermissionResponseDto);
    }


    @PostMapping("/search")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:manage')")
    public R<Page<SysUser>> searchSysUserList(@RequestBody SearchSysUserListRequestDto searchSysUserListRequestDto) {
        return R.success(userService.searchSysUserList(searchSysUserListRequestDto));
    }
}
