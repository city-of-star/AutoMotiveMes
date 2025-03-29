package com.automotivemes.controller.user;

import com.automotivemes.common.dto.user.*;
import com.automotivemes.common.response.CommonResponse;
import com.automotivemes.common.response.ResponseUtils;
import com.automotivemes.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Object>> register(@RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseUtils.okWithoutData();
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Object>> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = userService.login(loginRequest);
        return ResponseUtils.ok(authResponse);
    }

    @PostMapping("/info")
    public ResponseEntity<CommonResponse<Object>> info() {
        UserInfoResponse userInfoResponse = userService.getUserInfo();
        return ResponseUtils.ok(userInfoResponse);
    }

    @PostMapping("/getRoleAndPermission")
    public ResponseEntity<CommonResponse<Object>> getRoleAndPermission() {
        UserRoleAndPermissionResponse userRoleAndPermissionResponse = userService.getUserRoleAndPermission();
        return ResponseUtils.ok(userRoleAndPermissionResponse);
    }
}
