package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.SysPermissionTreeNodeVo;

import java.util.List;

/**
 * 实现功能【权限管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-30 14:18:29
 */
public interface PermissionService {

    // 获取权限树
    List<SysPermissionTreeNodeVo> getPermissionTree();
}