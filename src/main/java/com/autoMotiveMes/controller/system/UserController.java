package com.autoMotiveMes.controller.system;

import com.autoMotiveMes.common.dto.user.SearchSysUserListRequestDto;
import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.entity.user.SysUser;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/search")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:list')")
    public R<Page<SysUser>> searchSysUserList(@RequestBody SearchSysUserListRequestDto searchSysUserListRequestDto) {
        return R.success(userService.searchSysUserList(searchSysUserListRequestDto));
    }
}