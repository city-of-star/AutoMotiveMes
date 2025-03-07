package com.automotivemes.service.user;

import com.automotivemes.common.dto.user.*;

public interface UserService {
    void register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    UserInfoResponse getUserInfo(UserInfoRequest userInfoRequest);
}