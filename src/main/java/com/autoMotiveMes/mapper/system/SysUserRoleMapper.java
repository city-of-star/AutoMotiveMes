package com.autoMotiveMes.mapper.system;

import com.autoMotiveMes.entity.system.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 实现功能【用户角色关联表 mapper】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:49:56
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    @Delete("delete from sys_user_role where user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    @Select("select * from sys_user_role where user_id = #{userId}")
    SysUserRole selectByUserId(@Param("userId") Long userId);
}
