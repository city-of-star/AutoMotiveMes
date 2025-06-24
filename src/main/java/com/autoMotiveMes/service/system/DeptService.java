package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.DeptTreeNodeVo;
import com.autoMotiveMes.entity.system.SysDept;

import java.util.List;

/**
 * 实现功能【部门管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-05 18:18:37
 */
public interface DeptService {

    /**
     * 获取部门树
     * @return 部门树
     */
    List<DeptTreeNodeVo> getDeptTree();

    /**
     * 获取部门列表
     * @return 部门列表
     */
    List<SysDept> getDeptList();
}