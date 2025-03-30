package com.automotivemes.controller.user;

import com.automotivemes.common.dto.user.*;
import com.automotivemes.common.response.R;
import com.automotivemes.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
