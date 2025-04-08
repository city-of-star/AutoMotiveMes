package com.autoMotiveMes.config.security;

import com.autoMotiveMes.common.exception.AuthException;
import com.autoMotiveMes.common.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * 实现功能【基于角色的访问控制逻辑】
 *
 * @author li.hongyu
 * @date 2025-02-15 14:28:36
 */
@Slf4j
@Service("rbacService")
public class RbacService {

    public boolean hasPermission(Authentication authentication, String permissionCode) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("鉴权时发现用户 {} 未登录或者登录状态失效", authentication);
            throw new AuthException("登录信息过期，请重新登录");
        }

        boolean flag = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(perm -> perm.equals(permissionCode));

        if (flag) {  // 鉴权通过
            return true;
        } else {  // 鉴权失败
            log.warn("鉴权时发现用户 {} 无权访问 {}", authentication, permissionCode);
            throw new ForbiddenException("抱歉，您暂无权限访问");
        }
    }
}