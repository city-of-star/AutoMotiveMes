package com.autoMotiveMes.service.impl.auth;

import com.autoMotiveMes.common.exception.AuthException;
import com.autoMotiveMes.common.exception.BadRequestException;
import com.autoMotiveMes.config.security.UserDetailsImpl;
import com.autoMotiveMes.dto.auth.*;
import com.autoMotiveMes.entity.system.SysUser;
import com.autoMotiveMes.common.exception.GlobalException;
import com.autoMotiveMes.mapper.system.SysUserMapper;
import com.autoMotiveMes.service.auth.AuthService;
import com.autoMotiveMes.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void register(RegisterRequestDto registerRequestDto) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(registerRequestDto.getUsername()) != null) {
            throw new BadRequestException("注册失败，用户名已存在");
        }
        // 检查邮箱是否已存在
        if (userMapper.selectByEmail(registerRequestDto.getEmail()) != null) {
            throw new BadRequestException("注册失败，邮箱已存在");
        }
        // 检查两次输入的密码是否一致
        if (!Objects.equals(registerRequestDto.getPassword(), registerRequestDto.getConfirmPassword())) {
            throw new BadRequestException("注册失败，两次输入的密码不一致");
        }

        try {
            SysUser user = new SysUser();
            user.setUsername(registerRequestDto.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
            user.setEmail(registerRequestDto.getEmail());
            user.setStatus(1); // 默认启用状态
            user.setAccountLocked(1);  // 默认没有锁定
            user.setLoginAttempts(0);  // 初始化连续登陆失败次数为 0
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);

            log.info("用户 {} 注册成功", registerRequestDto.getUsername());
        } catch (Exception e) {
            throw new GlobalException("注册失败 || " + e.getMessage());
        }
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        // 验证用户的密码
        SysUser user = userMapper.selectByUsername(loginRequestDto.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new AuthException("抱歉，您的账号已停用");
        }
        if (user.getAccountLocked() == 0) {
            throw new AuthException("抱歉，您的账号已锁定");
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

            log.info("用户 {} 登录成功", loginRequestDto.getUsername());

            return response;
        } catch (Exception e) {
            throw new GlobalException("用户登录失败 || " + e.getMessage());
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
        throw new AuthException("登录信息过期，请重新登录");
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
        throw new AuthException("登录信息过期，请重新登录");
    }

    @Override
    public void isValidToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AuthException("无效的认证头");
        }

        String token = header.substring(7);
        jwtUtils.validateToken(token);
    }
}