package com.automotivemes.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * RbacService 类用于基于角色的访问控制（RBAC）权限验证。
 * 该类通过 Spring 的 @Service 注解标记为一个服务组件，名称为 "rbacService"，
 * 可以在 Spring Security 的配置中使用，用于验证用户是否有权限访问特定的 API 接口。
 */
@Service("rbacService")
public class RbacService {

    /**
     * 验证用户是否具有访问指定请求的权限。
     *
     * @param request        当前的 HTTP 请求，包含请求的方法和 URI 信息。
     * @param authentication 包含当前用户的认证信息，如用户的详细信息和权限列表。
     * @return 如果用户具有访问权限，则返回 true；否则返回 false。
     */
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        // 从 Authentication 对象中获取用户的主体信息
        Object principal = authentication.getPrincipal();
        // 检查主体是否为 UserDetails 类型
        if (principal instanceof UserDetails userDetails) {
            // 获取用户所有权限（已在 UserDetailsImpl 中加载）
            // 将用户的权限列表（GrantedAuthority 对象）转换为权限字符串列表
            List<String> permissions = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            // 获取当前请求的 HTTP 方法，如 GET、POST、PUT 等
            String method = request.getMethod();
            // 获取当前请求的 API 路径，即请求的 URI
            String apiPath = request.getRequestURI();

            // 模拟权限验证逻辑（需根据你的业务优化）
            // 检查用户的权限列表中是否包含当前请求的方法和路径的组合
            return permissions.stream()
                    .anyMatch(perm ->
                            perm.equalsIgnoreCase(method + ":" + apiPath));
        }
        // 如果主体不是 UserDetails 类型，则认为用户没有权限
        return false;
    }
}