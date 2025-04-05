package com.automotivemes.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * 实现功能【基于角色的访问控制逻辑】
 *
 * @author li.hongyu
 * @date 2025-02-15 14:28:36
 */
@Service("rbacService")
public class RbacService {

    public boolean hasPermission(Authentication authentication, String permissionCode) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(perm -> perm.equals(permissionCode));
    }
}