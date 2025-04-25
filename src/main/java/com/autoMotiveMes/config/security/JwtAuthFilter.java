package com.autoMotiveMes.config.security;

import com.autoMotiveMes.common.exception.BusinessException;
import com.autoMotiveMes.common.exception.ServerException;
import com.autoMotiveMes.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

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
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // 允许匿名访问的请求直接放行
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 验证头格式（保留原始逻辑，但改用更精确的异常）
        if (!authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid authorization header format");
        }

        String token = authHeader.substring(7);

        try {
            // 验证 Token 有效性（JwtUtils 内部已抛出具体异常）
            jwtUtils.validateToken(token);

            // 加载用户信息
            String username = jwtUtils.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 设置安全上下文
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            // 用户不存在（401 未授权）
            throw new BadCredentialsException("Invalid credentials", e);
        } catch (BusinessException | BadCredentialsException e) {
            // 已知的业务异常直接抛出（会被全局处理器捕获）
            throw e;
        } catch (Exception e) {
            // 其他未知异常包装为服务器异常
            throw new ServerException("认证过程失败: " + e.getMessage());
        }
    }
}