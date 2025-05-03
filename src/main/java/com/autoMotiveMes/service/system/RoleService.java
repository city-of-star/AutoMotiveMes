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

    /**
     * 获取角色列表
     * @return 角色列表
     */
    List<SysRole> getRoleList();

    /**
     * 分页查询角色列表
     * @param dto 查询条件
     * @return 分页角色列表
     */
    Page<SysRole> getRolePage(GetRolePageDto dto);

    /**
     * 根据角色id查询角色详细信息
     * @param roleId 角色id参数
     * @return 对应角色的详细信息
     */
    SysRole getRoleByRoleId(Integer roleId);

    /**
     * 获取指定角色关联的权限id列表
     * @param roleId 角色id参数
     * @return 该角色拥有的权限id列表
     */
    List<Integer> getPermIdsByRoleId(Integer roleId);

    /**
     * 新增角色
     * @param dto 角色信息参数
     */
    void addRole(AddRoleDto dto);

    /**
     * 更新角色信息
     * @param dto 角色更新参数
     */
    void updateRole(UpdateRoleDto dto);

    /**
     * 删除指定角色
     * @param dto 角色id参数
     */
    void deleteRole(DeleteRoleDto dto);
}