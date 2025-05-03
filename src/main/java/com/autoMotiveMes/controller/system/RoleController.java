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
 * 角色管理服务控制器
 * 实现角色管理相关接口，包括角色列表查询、分页查询、角色权限管理等功能
 *
 * @author li.hongyu
 * @date 2025-04-11 09:53:02
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取角色列表
     * @return 角色列表
     */
    @GetMapping("/list")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<List<SysRole>> getRoleList() {
        return R.success(roleService.getRoleList());
    }

    /**
     * 分页查询角色列表
     * @param dto 查询条件
     * @return 分页角色列表
     */
    @PostMapping("/page")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<Page<SysRole>> getRolePage(@RequestBody GetRolePageDto dto) {
        return R.success(roleService.getRolePage(dto));
    }

    /**
     * 根据角色id查询角色详细信息
     * @param roleId 角色id参数
     * @return 对应角色的详细信息
     */
    @GetMapping("/{roleId}")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<SysRole> getRoleById(@PathVariable Integer roleId) {
        return R.success(roleService.getRoleByRoleId(roleId));
    }

    /**
     * 获取指定角色关联的权限id列表
     * @param roleId 角色id参数
     * @return 该角色拥有的权限id列表
     */
    @GetMapping("/{roleId}/permIds")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<List<Integer>> getRolePermIds(@PathVariable Integer roleId) {
        return R.success(roleService.getPermIdsByRoleId(roleId));
    }

    /**
     * 新增角色
     * @param dto 角色信息参数
     * @return 操作结果（无具体数据返回）
     */
    @PostMapping("/add")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:add')")
    public R<?> addRole(@RequestBody AddRoleDto dto) {
        roleService.addRole(dto);
        return R.success();
    }

    /**
     * 更新角色信息
     * @param dto 角色更新参数
     * @return 操作结果（无具体数据返回）
     */
    @PostMapping("/update")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:update')")
    public R<?> updateRole(@RequestBody UpdateRoleDto dto) {
        roleService.updateRole(dto);
        return R.success();
    }

    /**
     * 删除指定角色
     * @param dto 角色id参数
     * @return 操作结果（无具体数据返回）
     */
    @PostMapping("/delete")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:delete')")
    public R<?> deleteRole(@RequestBody DeleteRoleDto dto) {
        roleService.deleteRole(dto);
        return R.success();
    }
}