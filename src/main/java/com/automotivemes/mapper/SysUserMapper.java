package com.automotivemes.mapper;

import com.automotivemes.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("SELECT p.perm_code FROM sys_user_role ur JOIN sys_role_permission rp ON ur.role_id = rp.role_id JOIN sys_permission p ON rp.perm_id = p.perm_id WHERE ur.user_id = #{userId}")
    List<String> selectUserPermissions(Long userId);

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(String username);
}
