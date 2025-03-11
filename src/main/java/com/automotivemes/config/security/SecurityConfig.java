package com.automotivemes.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置类，用于配置应用的安全策略，
 * 包括请求的访问规则、会话管理、密码编码器和认证管理器等。
 */
@Configuration
@EnableWebSecurity  // 启用 Spring Security 的 Web 安全功能
@EnableMethodSecurity  // 启用方法级别的安全控制，允许在服务层方法上使用 @PreAuthorize、@PostAuthorize 等注解
@RequiredArgsConstructor  // Lombok 注解，自动生成构造函数，用于注入所需的依赖
public class SecurityConfig {

    // 注入 JWT 认证过滤器
    private final JwtAuthFilter jwtAuthFilter;

    // 注入 Rbac 服务
    private final RbacService rbacService;

    /**
     * 配置安全过滤链，定义请求的访问规则、会话管理和过滤器顺序。
     *
     * @param http HttpSecurity 对象，用于配置 HTTP 安全相关的设置
     * @return 配置好的 SecurityFilterChain 对象
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（跨站请求伪造）保护，因为在无状态的应用中通常不需要
                .csrf(AbstractHttpConfigurer::disable)
                // 配置跨域
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 配置会话管理策略为无状态，即不使用 HTTP 会话来存储用户的认证信息
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置请求的授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许 /api/auth/login 和 /api/auth/register 这两个接口匿名访问
                        .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                        .requestMatchers("/api/equipment/real-time-data/simulate-create").permitAll()
                        .requestMatchers("/mes-websocket/**", "/topic/**", "/app/**").permitAll()
                        // 其他所有请求都需要进行身份验证
                        .anyRequest().authenticated()
                )
                // 在 UsernamePasswordAuthenticationFilter 之前添加自定义的 JWT 认证过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // 构建并返回配置好的安全过滤链
        return http.build();
    }

    /**
     * 创建并返回一个 BCrypt 密码编码器，用于对用户密码进行加密和验证。
     *
     * @return PasswordEncoder 对象
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 创建并返回一个认证管理器，用于处理用户的认证请求。
     *
     * @param config AuthenticationConfiguration 对象，用于获取认证管理器
     * @return AuthenticationManager 对象
     * @throws Exception 获取认证管理器过程中可能抛出的异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置跨域请求的源、方法、头信息等。
     *
     * @return CorsConfigurationSource 对象
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源的跨域请求
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        // 允许的 HTTP 方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 允许携带凭证（如 cookie）
        configuration.setAllowCredentials(true);
        // 允许 WebSocket 相关头部
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin"));
        // 预检请求的有效期（秒）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用跨域配置
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}