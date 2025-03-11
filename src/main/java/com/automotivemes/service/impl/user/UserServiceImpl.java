package com.automotivemes.service.impl.user;

import com.automotivemes.common.dto.user.*;
import com.automotivemes.entity.user.SysUser;
import com.automotivemes.common.exception.AuthException;
import com.automotivemes.mapper.user.SysUserMapper;
import com.automotivemes.service.user.UserService;
import com.automotivemes.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

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

        // 注册用户到数据库
        SysUser user = new SysUser();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setStatus(1); // 默认启用状态
        user.setAccountNonLocked(true);  // 默认没有锁定
        user.setLoginAttempts(0);  // 初始化连续登陆失败次数为 0
        user.setCreateTime(new Date());
        userMapper.insert(user);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // 验证用户的密码
        SysUser user = userMapper.selectByUsername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException("用户名或密码错误");
        }

        // 生成 token
        String token = jwtUtils.generateToken(loginRequest.getUsername());

        // 更新用户最后登录时间
        user.setLastLogin(new Date());
        userMapper.updateById(user);

        return new AuthResponse(token);
    }

    @Override
    public UserInfoResponse getUserInfo(UserInfoRequest userInfoRequest) {
        return userMapper.getUserInfoByUsername(userInfoRequest.getUsername());
    }
}