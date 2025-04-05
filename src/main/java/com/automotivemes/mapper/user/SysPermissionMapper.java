package com.automotivemes.mapper.user;

import com.automotivemes.entity.user.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 实现功能【权限表 mapper】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:47:36
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    List<SysPermission>  selectByRoleIds(Integer roleId);
}
