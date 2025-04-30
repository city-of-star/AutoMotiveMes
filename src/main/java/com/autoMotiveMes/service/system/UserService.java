package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.*;
import com.autoMotiveMes.entity.system.SysUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 实现功能【用户管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-05 17:55:03
 */
public interface UserService {
    // 查询用户列表
    Page<SysUser> searchSysUserList(SearchSysUserListDto searchSysUserListDto);

    // 删除用户
    void deleteUserByID(DeleteUserDto dto);

    // 切换用户状态
    void switchUserStatus(SwitchUserStatusDto dto);

    // 新增用户
    void addUser(AddUserDto dto);

    // 修改用户
    void updateUser(UpdateUserDto dto);

    // 通过userId获取用户信息(用于修改用户信息时获取用户原本信息)
    GetUserInfoVo getUserInfo(GetUserInfoDto dto);

    // 重置密码
    void resetPassword(ResetPasswordDto dto);
}