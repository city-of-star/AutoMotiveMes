package com.automotivemes.service.impl;

import com.automotivemes.common.dto.LoginRequest;
import com.automotivemes.common.dto.RegisterRequest;
import com.automotivemes.common.dto.AuthResponse;
import com.automotivemes.entity.SysUser;
import com.automotivemes.common.exception.AuthException;
import com.automotivemes.mapper.SysUserMapper;
import com.automotivemes.service.AuthService;
import com.automotivemes.config.security.UserDetailsImpl;
import com.automotivemes.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(registerRequest.getUsername()) != null) {
            throw new AuthException("注册失败，用户名已存在");
        }
        // 检查邮箱是否已存在
        if (userMapper.selectByEmail(registerRequest.getEmail()) != null) {
            throw new AuthException("注册失败，邮箱已存在");
        }
        // 检查两次输入的密码是否一致
        if (!Objects.equals(registerRequest.getPassword(), registerRequest.getConfirmPassword())) {
            throw new AuthException("注册失败，两次输入的密码不一致");
        }
        SysUser user = new SysUser();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setStatus(1); // 默认启用状态
        userMapper.insert(user);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        SysUser user = userMapper.selectByUsername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException("用户名或密码错误");
        }
        String token = jwtUtils.generateToken(UserDetailsImpl.build(user, userMapper.selectUserPermissions(user.getUserId())));
        return new AuthResponse(token);
    }
}