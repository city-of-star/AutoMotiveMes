package com.autoMotiveMes.service.impl.system;

import com.autoMotiveMes.dto.system.DeptTreeNodeVo;
import com.autoMotiveMes.dto.system.DeptTreeNodeVo;
import com.autoMotiveMes.entity.system.SysDept;
import com.autoMotiveMes.mapper.system.SysDeptMapper;
import com.autoMotiveMes.service.system.DeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 实现功能【部门管理服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-05 18:19:13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final SysDeptMapper sysDeptMapper;

    @Override
    public List<DeptTreeNodeVo> getDeptTree() {
        List<SysDept> depts = sysDeptMapper.selectAllEnabledDepts();
        return buildTree(depts);
    }

    @Override
    public List<SysDept> getDeptList() {
        return sysDeptMapper.selectList(null);
    }

    private List<DeptTreeNodeVo> buildTree(List<SysDept> depts) {
        Map<Long, DeptTreeNodeVo> nodeMap = new LinkedHashMap<>();

        // 创建所有节点
        depts.forEach(dept -> {
            DeptTreeNodeVo node = new DeptTreeNodeVo();
            node.setDeptId(dept.getDeptId());
            node.setDeptName(dept.getDeptName());
            node.setParentId(dept.getParentId());
            node.setOrderNum(dept.getOrderNum());
            nodeMap.put(dept.getDeptId(), node);
        });

        // 构建树结构
        List<DeptTreeNodeVo> trees = new ArrayList<>();
        nodeMap.values().forEach(node -> {
            if (node.getParentId() == null || node.getParentId() == 0) {
                trees.add(node);
            } else {
                DeptTreeNodeVo parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(node);
                }
            }
        });

        // 按order_num排序
        sortTree(trees);
        return trees;
    }

    private void sortTree(List<DeptTreeNodeVo> nodes) {
        nodes.sort(Comparator.comparingInt(DeptTreeNodeVo::getOrderNum));
        nodes.forEach(node -> {
            if (node.getChildren() != null) {
                sortTree(node.getChildren());
            }
        });
    }
}