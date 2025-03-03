package com.automotivemes.controller;

import com.automotivemes.common.dto.*;
import com.automotivemes.common.response.CommonResponse;
import com.automotivemes.common.response.ResponseUtils;
import com.automotivemes.service.UserService;
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

    @PreAuthorize("@rbacService.hasPermission(authentication, 'user:manage')")
    @PostMapping("/info")
    public ResponseEntity<CommonResponse<Object>> info(@RequestBody UserInfoRequest infoRequest) {
        UserInfoResponse userInfoResponse = userService.getUserInfo(infoRequest);
        return ResponseUtils.ok(userInfoResponse);
    }
}
