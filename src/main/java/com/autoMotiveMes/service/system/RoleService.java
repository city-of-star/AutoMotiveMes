package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.AddRoleDto;
import com.autoMotiveMes.dto.system.DeleteRoleDto;
import com.autoMotiveMes.dto.system.GetRolePageDto;
import com.autoMotiveMes.dto.system.UpdateRoleDto;
import com.autoMotiveMes.entity.system.SysRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 实现功能【角色管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-11 09:46:38
 */
public interface RoleService {

    // 获取角色列表
    List<SysRole> getRoleList();

    // 分页查询角色列表
    Page<SysRole> getRolePage(GetRolePageDto dto);

    // 根据角色ID获取角色信息
    SysRole getRoleByRoleId(Integer roleId);

    // 根据角色ID获取权限ID列表
    List<Integer> getPermIdsByRoleId(Integer roleId);

    // 新增角色
    void addRole(AddRoleDto dto);

    // 修改角色
    void updateRole(UpdateRoleDto dto);

    // 删除角色
    void deleteRole(DeleteRoleDto dto);
}