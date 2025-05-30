package com.autoMotiveMes.config.security;

import com.autoMotiveMes.entity.system.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实现功能【用户认证信息封装类】
 *
 * @author li.hongyu
 * @date 2025-02-15 10:13:27
 */
@Data
public class UserDetailsImpl implements UserDetails {

    private Long userId;
    private String username;
    private String password;
    private Integer status;
    private Integer accountNonLocked;
    private List<String> Roles;
    private List<GrantedAuthority> authorities;

    // 通过用户实体构建 UserDetailsImpl
    public static UserDetailsImpl build(SysUser user, List<String> roles, List<String> permissions) {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUserId(user.getUserId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setStatus(user.getStatus());
        userDetails.setAccountNonLocked(user.getAccountLocked());
        userDetails.setRoles(roles);
        userDetails.authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return userDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }  // 账号永不会过期

    @Override
    public boolean isAccountNonLocked() { return accountNonLocked == 1; }  // 状态 1 表示未锁定（返回 true 表示账号永不会锁定）

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }  // 用户的密码永不会过期

    @Override
    public boolean isEnabled() {
        return status == 1;
    }  // 状态 1 表示启用（返回 true 表示账号永远启用）
}