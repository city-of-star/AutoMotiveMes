package com.autoMotiveMes.mapper.system;

import com.autoMotiveMes.entity.system.SysRolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【角色权限关联表 mapper】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:49:12
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    void deleteByRoleId(@Param("roleId") Integer roleId);

    List<Integer> selectPermIdsByRoleId(@Param("roleId") Integer roleId);
}
