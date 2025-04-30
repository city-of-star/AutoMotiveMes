package com.autoMotiveMes.service.impl.system;

import com.autoMotiveMes.common.exception.BusinessException;
import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.dto.system.AddRoleDto;
import com.autoMotiveMes.dto.system.DeleteRoleDto;
import com.autoMotiveMes.dto.system.GetRolePageDto;
import com.autoMotiveMes.dto.system.UpdateRoleDto;
import com.autoMotiveMes.entity.system.SysRole;
import com.autoMotiveMes.entity.system.SysRolePermission;
import com.autoMotiveMes.entity.system.SysUserRole;
import com.autoMotiveMes.mapper.system.SysRoleMapper;
import com.autoMotiveMes.mapper.system.SysRolePermissionMapper;
import com.autoMotiveMes.mapper.system.SysUserRoleMapper;
import com.autoMotiveMes.service.system.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现功能【角色管理服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-11 09:49:08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public List<SysRole> getRoleList() {
        return roleMapper.selectList(null);
    }

    @Override
    public Page<SysRole> getRolePage(GetRolePageDto dto) {
        Page<SysRole> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize());

        return roleMapper.getRolePage(page, dto);
    }

    @Override
    public SysRole getRoleByRoleId(Integer roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    public List<Integer> getPermIdsByRoleId(Integer roleId) {
        return rolePermissionMapper.selectPermIdsByRoleId(roleId);
    }

    @Override
    public void addRole(AddRoleDto dto) {
        if (roleMapper.selectOne(new QueryWrapper<SysRole>().eq("role_name", dto.getRoleName())) != null) {
            throw new BusinessException(ErrorCode.ROLE_NAME_EXISTS);
        }
        if (roleMapper.selectOne(new QueryWrapper<SysRole>().eq("role_code", dto.getRoleCode())) != null) {
            throw new BusinessException(ErrorCode.ROLE_CODE_EXISTS);
        }

        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setDescription(dto.getDescription());
        roleMapper.insert(role);

        Integer roleId = roleMapper.selectOne(
                new QueryWrapper<SysRole>().eq("role_code", dto.getRoleCode())).getRoleId();

        for (Integer permId : dto.getPermIds()) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setRoleId(roleId);
            sysRolePermission.setPermId(permId);
            rolePermissionMapper.insert(sysRolePermission);
        }
    }

    @Override
    public void updateRole(UpdateRoleDto dto) {
        SysRole role = new SysRole();
        role.setRoleId(dto.getRoleId());
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setDescription(dto.getDescription());
        roleMapper.updateById(role);

        rolePermissionMapper.deleteByRoleId(dto.getRoleId());

        for (Integer permId : dto.getPermIds()) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setRoleId(dto.getRoleId());
            sysRolePermission.setPermId(permId);
            rolePermissionMapper.insert(sysRolePermission);
        }
    }

    @Override
    public void deleteRole(DeleteRoleDto dto) {
        Long count = userRoleMapper.selectCount(
                new QueryWrapper<SysUserRole>().eq("role_id", dto.getRoleId()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.ROLE_IS_USE);
        }

        rolePermissionMapper.deleteByRoleId(dto.getRoleId());
        roleMapper.deleteById(dto.getRoleId());
    }
}