package com.automotivemes.service.user;

import com.automotivemes.common.dto.user.*;
import com.automotivemes.entity.user.SysUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface UserService {
    void register(RegisterRequestDto registerRequestDto);
    AuthResponseDto login(LoginRequestDto loginRequestDto);
    UserInfoResponseDto getUserInfo();
    UserRoleAndPermissionResponseDto getUserRoleAndPermission();

    Page<SysUser> searchSysUserList(SearchSysUserListRequestDto searchSysUserListRequestDto);
}