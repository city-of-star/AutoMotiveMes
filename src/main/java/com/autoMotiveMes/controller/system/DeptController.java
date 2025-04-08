package com.autoMotiveMes.controller.system;

import com.autoMotiveMes.dto.dept.SysDeptTreeNode;
import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.service.system.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 实现功能【部门管理服务controller】
 *
 * @author li.hongyu
 * @date 2025-04-05 21:03:35
 */
@RestController
@RequestMapping("/api/dept")
@RequiredArgsConstructor
public class DeptController {
    private final DeptService deptService;

    @GetMapping("/tree")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:dept:list')")
    public R<List<SysDeptTreeNode>> getDeptTree() {
        return R.success(deptService.getDeptTree());
    }
}