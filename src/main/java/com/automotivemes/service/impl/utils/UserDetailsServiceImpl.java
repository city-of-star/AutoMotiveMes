package com.automotivemes.service.impl.utils;

import com.automotivemes.entity.SysUser;
import com.automotivemes.mapper.SysUserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;

    public UserDetailsServiceImpl(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户基本信息
        SysUser user = userMapper.selectByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 查询用户权限列表
        List<String> permissions = userMapper.selectUserPermissions(user.getUserId());

        return UserDetailsImpl.build(user, permissions);
    }
}
