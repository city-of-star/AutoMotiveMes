package com.autoMotiveMes.service.impl.system;

import com.autoMotiveMes.dto.system.SysPermissionTreeNode;
import com.autoMotiveMes.entity.system.SysPermission;
import com.autoMotiveMes.mapper.system.SysPermissionMapper;
import com.autoMotiveMes.service.system.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 实现功能【权限管理服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-30 14:19:13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysPermissionMapper permissionMapper;

    @Override
    public List<SysPermissionTreeNode> getPermissionTree() {
        // 查询所有权限并按排序号升序排列
        List<SysPermission> permissions = permissionMapper.selectList(
                new QueryWrapper<SysPermission>().orderByAsc("order_num"));
        // 转换为树节点并构建树结构
        return buildPermissionTree(permissions);
    }

    private List<SysPermissionTreeNode> buildPermissionTree(List<SysPermission> permissions) {
        // 将权限实体转换为树节点
        List<SysPermissionTreeNode> nodes = permissions.stream()
                .map(this::convertToTreeNode)
                .toList();

        // 创建父ID到子节点列表的映射
        Map<Integer, List<SysPermissionTreeNode>> parentChildrenMap = new HashMap<>();
        nodes.forEach(node -> {
            int parentId = node.getParentId();
            parentChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>())
                    .add(node);
        });

        // 获取根节点（parentId = 0）
        List<SysPermissionTreeNode> rootNodes = parentChildrenMap.getOrDefault(0, new ArrayList<>());

        // 为每个节点递归设置子节点
        rootNodes.forEach(node -> populateChildren(node, parentChildrenMap));

        return rootNodes;
    }

    private void populateChildren(SysPermissionTreeNode node, Map<Integer, List<SysPermissionTreeNode>> parentChildrenMap) {
        Integer permId = node.getPermId();
        List<SysPermissionTreeNode> children = parentChildrenMap.get(permId);
        if (children != null) {
            // 对子节点按order_num排序
            children.sort(Comparator.comparingInt(SysPermissionTreeNode::getOrderNum));
            node.setChildren(children);
            // 递归处理子节点的子节点
            children.forEach(child -> populateChildren(child, parentChildrenMap));
        }
    }

    private SysPermissionTreeNode convertToTreeNode(SysPermission permission) {
        SysPermissionTreeNode node = new SysPermissionTreeNode();
        node.setPermId(permission.getPermId());
        node.setPermCode(permission.getPermCode());
        node.setPermName(permission.getPermName());
        node.setPermType(permission.getPermType());
        node.setParentId(permission.getParentId());
        node.setPath(permission.getPath());
        node.setComponent(permission.getComponent());
        node.setIcon(permission.getIcon());
        node.setOrderNum(permission.getOrderNum());
        node.setApiPath(permission.getApiPath());
        node.setMethod(permission.getMethod());
        return node;
    }
}