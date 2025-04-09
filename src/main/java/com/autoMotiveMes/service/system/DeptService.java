package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.SysDeptTreeNode;

import java.util.List;

/**
 * 实现功能【部门管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-05 18:18:37
 */
public interface DeptService {
    List<SysDeptTreeNode> getDeptTree();
}