package com.autoMotiveMes.controller.system;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.system.GetRoleListResponseDto;
import com.autoMotiveMes.entity.system.SysRole;
import com.autoMotiveMes.service.system.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 实现功能【角色管理服务controller】
 *
 * @author hu.hongdou
 * @date 2025-04-11 09:53:02
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<GetRoleListResponseDto> getRoleList() {
        return R.success(roleService.getRoleList());
    }
}