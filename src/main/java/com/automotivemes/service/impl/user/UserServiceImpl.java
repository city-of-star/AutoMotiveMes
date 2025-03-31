package com.automotivemes.service.impl.user;

import com.automotivemes.common.dto.user.*;
import com.automotivemes.common.exception.AuthException;
import com.automotivemes.common.exception.BadRequestException;
import com.automotivemes.config.security.UserDetailsImpl;
import com.automotivemes.entity.user.SysUser;
import com.automotivemes.common.exception.GlobalException;
import com.automotivemes.mapper.user.SysUserMapper;
import com.automotivemes.service.user.UserService;
import com.automotivemes.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void register(RegisterRequestDto registerRequestDto) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(registerRequestDto.getUsername()) != null) {
            throw new BadRequestException("用户注册失败，用户名已存在");
        }
        // 检查邮箱是否已存在
        if (userMapper.selectByEmail(registerRequestDto.getEmail()) != null) {
            throw new BadRequestException("用户注册失败，邮箱已存在");
        }
        // 检查两次输入的密码是否一致
        if (!Objects.equals(registerRequestDto.getPassword(), registerRequestDto.getConfirmPassword())) {
            throw new BadRequestException("用户注册失败，两次输入的密码不一致");
        }

        try {
            SysUser user = new SysUser();
            user.setUsername(registerRequestDto.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
            user.setEmail(registerRequestDto.getEmail());
            user.setStatus(1); // 默认启用状态
            user.setAccountLocked(true);  // 默认没有锁定
            user.setLoginAttempts(0);  // 初始化连续登陆失败次数为 0
            user.setCreateTime(new Date());
            userMapper.insert(user);

            log.info("---------- 用户注册成功: {} ----------", registerRequestDto);
        } catch (Exception e) {
            throw new GlobalException("用户注册失败 || " + e.getMessage());
        }
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        // 验证用户的密码
        SysUser user = userMapper.selectByUsername(loginRequestDto.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }

        try {
            // 查询用户的角色和权限
            List<String> roles = userMapper.selectUserRoles(user.getUsername());
            List<String> permissions = userMapper.selectUserPermissions(user.getUsername());

            // 生成 token
            String token = jwtUtils.generateToken(loginRequestDto.getUsername());

            // 创建响应对象并设置角色和权限
            AuthResponseDto response = new AuthResponseDto();
            response.setToken(token);
            response.setRoles(roles.toArray(new String[0]));
            response.setPermissions(permissions.toArray(new String[0]));

            // 更新用户最后登录时间
            Date date = new Date();
            user.setLastLogin(date);
            userMapper.updateById(user);

            log.info("---------- 用户登录成功: {用户名：{}，登录时间：{}} ----------",
                    loginRequestDto.getUsername(), date);

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
        throw new AuthException("用户尝试获取个人信息，但是用户未认证或认证失败");
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
        throw new AuthException("用户尝试获取个人角色和权限，但是用户未认证或认证失败");
    }
}