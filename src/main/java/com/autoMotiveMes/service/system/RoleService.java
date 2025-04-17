package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.GetRoleListResponseDto;
import com.autoMotiveMes.entity.system.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 实现功能【角色管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-11 09:46:38
 */
public interface RoleService {

    // 获取角色列表
    GetRoleListResponseDto getRoleList();
}