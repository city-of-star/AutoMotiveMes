package com.automotivemes.service.user;

import com.automotivemes.common.dto.user.*;
import com.automotivemes.entity.user.SysUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 实现功能【用户认证服务接口】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:46:31
 */
public interface UserService {
    void register(RegisterRequestDto registerRequestDto);
    AuthResponseDto login(LoginRequestDto loginRequestDto);
    UserInfoResponseDto getUserInfo();
    UserRoleAndPermissionResponseDto getUserRoleAndPermission();

    Page<SysUser> searchSysUserList(SearchSysUserListRequestDto searchSysUserListRequestDto);
}