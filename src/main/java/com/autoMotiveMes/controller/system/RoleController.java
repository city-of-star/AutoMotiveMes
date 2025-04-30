package com.autoMotiveMes.controller.system;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.system.AddRoleDto;
import com.autoMotiveMes.dto.system.DeleteRoleDto;
import com.autoMotiveMes.dto.system.GetRolePageDto;
import com.autoMotiveMes.dto.system.UpdateRoleDto;
import com.autoMotiveMes.entity.system.SysRole;
import com.autoMotiveMes.service.system.RoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现功能【角色管理服务controller】
 *
 * @author li.hongyu
 * @date 2025-04-11 09:53:02
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<List<SysRole>> getRoleList() {
        return R.success(roleService.getRoleList());
    }

    @PostMapping("/page")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<Page<SysRole>> getRolePage(@RequestBody GetRolePageDto dto) {
        return R.success(roleService.getRolePage(dto));
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<SysRole> getRoleById(@PathVariable Integer roleId) {
        return R.success(roleService.getRoleByRoleId(roleId));
    }

    @GetMapping("/{roleId}/permIds")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<List<Integer>> getRolePermIds(@PathVariable Integer roleId) {
        return R.success(roleService.getPermIdsByRoleId(roleId));
    }

    @PostMapping("/add")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:add')")
    public R<?> addRole(@RequestBody AddRoleDto dto) {
        roleService.addRole(dto);
        return R.success();
    }

    @PostMapping("/update")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:update')")
    public R<?> updateRole(@RequestBody UpdateRoleDto dto) {
        roleService.updateRole(dto);
        return R.success();
    }

    @PostMapping("/delete")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:delete')")
    public R<?> deleteRole(@RequestBody DeleteRoleDto dto) {
        roleService.deleteRole(dto);
        return R.success();
    }
}