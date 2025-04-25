package com.autoMotiveMes.config.security;

import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service("rbacService")
public class RbacService {

    public boolean hasPermission(Authentication authentication, String permissionCode) {
        // 检查认证状态
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException(ErrorCode.LOGIN_INFO_EXPIRED.getMsg());
        }

        // 权限校验逻辑
        boolean hasPermission = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(perm -> perm.equals(permissionCode));

        if (!hasPermission) {
            log.warn("权限校验失败: user={}, required={}", authentication.getName(), permissionCode);
            throw new AccessDeniedException(ErrorCode.NOT_PERMISSION.getMsg());  // 无权访问
        }
        return true;
    }
}