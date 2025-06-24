package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.PermissionTreeNodeVo;

import java.util.List;

/**
 * 实现功能【权限管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-30 14:18:29
 */
public interface PermissionService {

    /**
     * 获取权限树
     * @return 权限树
     */
    List<PermissionTreeNodeVo> getPermissionTree();
}