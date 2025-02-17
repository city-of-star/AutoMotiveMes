package com.automotivemes.controller;

import com.automotivemes.common.response.CommonResponse;
import com.automotivemes.service.AuthService;
import com.automotivemes.common.dto.LoginRequest;
import com.automotivemes.common.dto.RegisterRequest;
import com.automotivemes.common.dto.AuthResponse;
import com.automotivemes.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Object>> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseUtil.okWithoutData();
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Object>> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseUtil.ok(authResponse);
    }
}
