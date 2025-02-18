package com.automotivemes.config.security;

import com.automotivemes.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * JwtAuthFilter 是一个过滤器，用于在每个请求中验证 JWT（JSON Web Token）。
 * 它继承自 OncePerRequestFilter，确保每个请求只被过滤一次。
 * 该过滤器会从请求头中提取 JWT，验证其有效性，并将用户信息设置到 Spring Security 的上下文当中。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    // JwtUtils 工具类，用于处理 JWT 的生成、验证和解析等操作
    private final JwtUtils jwtUtils;
    // UserDetailsService 用于根据用户名加载用户详细信息
    private final UserDetailsService userDetailsService;

    /**
     * 此方法是过滤器的核心逻辑，用于处理每个传入的请求。
     * 它会检查请求头中的 Authorization 字段，提取 JWT 并验证其有效性。
     * 如果 JWT 有效，它会将用户信息设置到 Spring Security 的上下文当中。
     *
     * @param request     客户端发送的 HTTP 请求
     * @param response    服务器返回的 HTTP 响应
     * @param filterChain 过滤器链，用于将请求传递给下一个过滤器或目标资源
     * @throws ServletException 如果在处理请求时发生 Servlet 相关的异常
     * @throws IOException      如果在处理请求或响应时发生 I/O 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取 Authorization 字段的值
        String authHeader = request.getHeader("Authorization");
        // 检查 Authorization 字段是否为空或者是否以 "Bearer " 开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 如果不满足条件，直接将请求传递给下一个过滤器或目标资源
            filterChain.doFilter(request, response);
            return;
        }

        // 提取出 JWT（去除 "Bearer " 前缀）
        String token = authHeader.substring(7);
        // 使用 JwtUtils 工具类验证 JWT 的有效性
        if (jwtUtils.validateToken(token)) {
            // 从 JWT 中解析出用户名
            String username = jwtUtils.getUsernameFromToken(token);
            // 根据用户名从 UserDetailsService 中加载用户详细信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 创建一个 UsernamePasswordAuthenticationToken 对象，用于表示用户的认证信息
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    // 用户的详细信息
                    userDetails,
                    // 密码，这里设置为 null，因为 JWT 验证不依赖密码
                    null,
                    // 用户的权限信息
                    userDetails.getAuthorities());
            // 设置认证信息的详细信息，包括请求的 IP 地址、会话 ID 等
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 将认证信息设置到 Spring Security 的上下文当中，表示用户已经通过认证
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 将请求传递给下一个过滤器或目标资源
        filterChain.doFilter(request, response);
    }
}