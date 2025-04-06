package com.autoMotiveMes.config.security;

import com.autoMotiveMes.entity.user.SysUser;
import com.autoMotiveMes.mapper.user.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现功能【用户认证业务实现类】
 *
 * @author li.hongyu
 * @date 2025-02-15 10:15:49
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户基本信息
        SysUser user = userMapper.selectByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 查询用户角色和权限列表
        List<String> permissions = userMapper.selectUserPermissions(user.getUsername());
        List<String> roles = userMapper.selectUserRoles(user.getUsername());

        return UserDetailsImpl.build(user, roles, permissions);
    }
}
