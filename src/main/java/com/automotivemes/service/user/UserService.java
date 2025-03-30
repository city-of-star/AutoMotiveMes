package com.automotivemes.service.user;

import com.automotivemes.common.dto.user.*;

public interface UserService {
    void register(RegisterRequestDto registerRequestDto);
    AuthResponseDto login(LoginRequestDto loginRequestDto);
    UserInfoResponseDto getUserInfo();
    UserRoleAndPermissionResponseDto getUserRoleAndPermission();
}