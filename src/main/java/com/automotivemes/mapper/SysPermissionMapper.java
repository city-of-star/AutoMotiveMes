package com.automotivemes.mapper;

import com.automotivemes.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    @Select("SELECT * FROM sys_permission WHERE perm_id IN (SELECT perm_id FROM sys_role_permission WHERE role_id IN (SELECT role_id FROM sys_user_role WHERE user_id = (SELECT user_id FROM sys_user WHERE username = #{username})))")
    List<SysPermission> selectByUser(String username);
}
