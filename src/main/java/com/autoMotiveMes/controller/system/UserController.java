package com.autoMotiveMes.controller.system;

import com.autoMotiveMes.dto.system.*;
import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.entity.system.SysUser;
import com.autoMotiveMes.service.system.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【用户管理服务controller】
 *
 * @author li.hongyu
 * @date 2025-04-05 17:59:04
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/search")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:list')")
    public R<Page<SysUser>> searchSysUserList(@RequestBody SearchSysUserListRequestDto searchSysUserListRequestDto) {
        return R.success(userService.searchSysUserList(searchSysUserListRequestDto));
    }

    @PostMapping("/delete")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:delete')")
    public R<?> deleteSysUser(@RequestBody DeleteUserRequestDto dto) {
        userService.deleteUserByID(dto);
        return R.success();
    }

    @PostMapping("/switchStatus")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:manage')")
    public R<?> switchUserStatus(@RequestBody SwitchUserStatusRequestDto dto) {
        userService.switchUserStatus(dto);
        return R.success();
    }

    @PostMapping("/add")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:add')")
    public R<?> addUser(@RequestBody AddUserRequestDto dto) {
        userService.addUser(dto);
        return R.success();
    }

    @PostMapping("/update")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:update')")
    public R<?> updateUser(@RequestBody UpdateUserRequestDto dto) {
        userService.updateUser(dto);
        return R.success();
    }

    @PostMapping("/getInfo")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:manage')")
    public R<?> getUserInfo(@RequestBody GetUserInfoRequestDto dto) {
        return R.success(userService.getUserInfo(dto));
    }
}