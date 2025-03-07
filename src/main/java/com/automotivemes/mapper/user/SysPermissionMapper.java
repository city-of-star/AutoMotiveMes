package com.automotivemes.mapper.user;

import com.automotivemes.entity.user.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.security.Permission;
import java.util.List;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    List<Permission>  selectByRoleIds(Integer roleId);
}
