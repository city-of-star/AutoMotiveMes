package com.automotivemes.service;

import com.automotivemes.common.dto.LoginRequest;
import com.automotivemes.common.dto.RegisterRequest;
import com.automotivemes.common.dto.AuthResponse;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}