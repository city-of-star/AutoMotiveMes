package com.autoMotiveMes.service.impl.system;

import com.autoMotiveMes.dto.system.GetRoleListResponseDto;
import com.autoMotiveMes.entity.system.SysRole;
import com.autoMotiveMes.mapper.system.SysRoleMapper;
import com.autoMotiveMes.service.system.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现功能【角色管理服务实现类】
 *
 * @author hu.hongdou
 * @date 2025-04-11 09:49:08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper roleMapper;

    @Override
    public GetRoleListResponseDto getRoleList() {
        GetRoleListResponseDto dto = new GetRoleListResponseDto();
        dto.setRoles(roleMapper.selectList(null));
        return dto;
    }
}