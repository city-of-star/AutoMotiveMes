package com.autoMotiveMes.service.impl.auth;

import com.autoMotiveMes.common.exception.BusinessException;
import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.config.security.UserDetailsImpl;
import com.autoMotiveMes.dto.auth.*;
import com.autoMotiveMes.entity.system.SysUser;
import com.autoMotiveMes.common.exception.ServerException;
import com.autoMotiveMes.mapper.system.SysUserMapper;
import com.autoMotiveMes.service.auth.AuthService;
import com.autoMotiveMes.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能【用户认证服务实现类】
 *
 * @author li.hongyu
 * @date 2025-02-15 16:57:15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // jwt 过期时间，用户信息缓存过期时间
    @Value("${security.jwt.expiration}")
    private int expiration;
    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, SysUser> userRedisTemplate;

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        // 验证用户的密码
        SysUser user = userMapper.selectByUsername(loginRequestDto.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.ERROR_USERNAME_OR_PASSWORD);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        if (user.getAccountLocked() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        try {
            // 生成 token
            String token = jwtUtils.generateToken(loginRequestDto.getUsername());

            // 创建响应对象并设置角色和权限
            AuthResponseDto response = new AuthResponseDto();
            response.setToken(token);

            // 更新用户最后登录时间
            LocalDateTime date = LocalDateTime.now();
            user.setLastLogin(date);
            userMapper.updateById(user);

            // 将用户信息存入 Redis，有效期与 Token 一致
            userRedisTemplate.opsForValue().set(
                    "user:" + user.getUsername(),
                    user,
                    // 添加随机偏移量，避免大量缓存同时失效
                    expiration + new Random().nextInt(300),
                    TimeUnit.SECONDS
            );

            log.info("用户 {} 登录成功", loginRequestDto.getUsername());

            return response;
        } catch (Exception e) {
            throw new ServerException("用户登录失败 || " + e.getMessage());
        }
    }

    @Override
    public UserInfoResponseDto getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return userMapper.getUserInfoByUsername(authentication.getName());
        }
        if (authentication != null) {
            log.warn("用户 {} 尝试获取个人信息，但是用户未认证或认证失败", authentication.getName());
        }
        throw new BadCredentialsException(ErrorCode.LOGIN_INFO_EXPIRED.getMsg());
    }

    @Override
    public UserRoleAndPermissionResponseDto getUserRoleAndPermission() {
        UserRoleAndPermissionResponseDto userRoleAndPermission = new UserRoleAndPermissionResponseDto();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetailsImpl userDetailsImpl = (UserDetailsImpl) principal;
                userRoleAndPermission.setRoles(userDetailsImpl.getRoles().toArray(new String[0]));
                userRoleAndPermission.setPermissions(
                        userDetailsImpl.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toArray(String[]::new)
                );
                return userRoleAndPermission;
            }
        }
        if (authentication != null) {
            log.warn("用户 {} 尝试获取个人角色和权限，但是用户未认证或认证失败", authentication.getName());
        }
        throw new BadCredentialsException(ErrorCode.LOGIN_INFO_EXPIRED.getMsg());
    }

    @Override
    public void isValidToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BadCredentialsException(ErrorCode.ERROR_AUTHENTICATION_HEADER.getMsg());
        }

        String token = header.substring(7);
        jwtUtils.validateToken(token);
    }
}