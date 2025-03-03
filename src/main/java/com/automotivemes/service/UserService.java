package com.automotivemes.service;

import com.automotivemes.common.dto.*;

public interface UserService {
    void register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    UserInfoResponse getUserInfo(UserInfoRequest userInfoRequest);
}