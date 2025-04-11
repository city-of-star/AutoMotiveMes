package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.AddUserRequestDto;
import com.autoMotiveMes.dto.system.DeleteUserRequestDto;
import com.autoMotiveMes.dto.system.SearchSysUserListRequestDto;
import com.autoMotiveMes.dto.system.SwitchUserStatusRequestDto;
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
    Page<SysUser> searchSysUserList(SearchSysUserListRequestDto searchSysUserListRequestDto);

    // 删除用户
    void deleteUserByID(DeleteUserRequestDto dto);

    // 切换用户状态
    void switchUserStatus(SwitchUserStatusRequestDto dto);

    // 新增用户
    void addUser(AddUserRequestDto dto);
}