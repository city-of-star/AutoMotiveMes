package com.automotivemes.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service("rbacService")
public class RbacService {

    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            // 获取用户所有权限（已在 UserDetailsImpl 中加载）
            List<String> permissions = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            // 获取当前请求的API路径和方法
            String method = request.getMethod();
            String apiPath = request.getRequestURI();

            // 模拟权限验证逻辑（需根据你的业务优化）
            return permissions.stream()
                    .anyMatch(perm ->
                            perm.equalsIgnoreCase(method + ":" + apiPath));
        }
        return false;
    }
}
