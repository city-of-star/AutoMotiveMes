package com.autoMotiveMes.controller.system;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.system.PermissionTreeNodeVo;
import com.autoMotiveMes.service.system.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 实现功能【权限管理服务 controller】
 *
 * @author li.hongyu
 * @date 2025-04-30 14:20:37
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system/permission")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/tree")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:role:list')")
    public R<List<PermissionTreeNodeVo>> getDeptTree() {
        return R.success(permissionService.getPermissionTree());
    }
}