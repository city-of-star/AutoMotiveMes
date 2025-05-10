package com.autoMotiveMes.mapper.system;

import com.autoMotiveMes.dto.system.GetRolePageDto;
import com.autoMotiveMes.entity.system.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【角色表 mapper】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:46:38
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    // 分页查询角色列表
    Page<SysRole> getRolePage(Page<SysRole> page, @Param("dto") GetRolePageDto dto);

    // 根据角色名查询部门id
    Integer getRoleIdByName(@Param("roleName") String roleName);
}
