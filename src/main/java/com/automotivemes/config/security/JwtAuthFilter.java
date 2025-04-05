package com.automotivemes.config.security;

import com.automotivemes.common.exception.AuthException;
import com.automotivemes.common.exception.GlobalException;
import com.automotivemes.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 实现功能【jwt 过滤器】
 *
 * @author li.hongyu
 * @date 2025-02-15 13:47:28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // 无认证头时允许匿名访问
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 验证Authorization头格式
        if (!authHeader.startsWith("Bearer ")) {
            log.warn("无效的Authorization头格式，期望Bearer token");
            throw new AuthException("无效的认证头");
        }

        String token = authHeader.substring(7);
        if (token.isEmpty()) {
            log.error("Token为空");
            throw new AuthException("Token为空");
        }

        try {
            // 验证Token有效性
            if (!jwtUtils.validateToken(token)) {
                throw new AuthException("Token无效或已过期");
            }

            // 从Token中解析用户名并加载用户信息
            String username = jwtUtils.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 设置安全上下文
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("用户 {} 认证成功", username);
            }

            // 继续执行后续过滤器
            filterChain.doFilter(request, response);
        } catch (AuthException e) {
            log.error("认证失败: {}", e.getMessage());
            throw new AuthException("认证失败: " + e.getMessage());
        } catch (UsernameNotFoundException e) {
            log.error("用户不存在: {}", e.getMessage());
            throw new AuthException("用户不存在: " + e.getMessage());
        } catch (Exception e) {
            log.error("处理认证时发生未知错误", e);
            throw new GlobalException("处理认证时发生未知错误: " + e.getMessage());
        }
    }
}