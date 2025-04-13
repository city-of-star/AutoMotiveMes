package com.autoMotiveMes.mapper.system;

import com.autoMotiveMes.dto.system.SearchSysUserListRequestDto;
import com.autoMotiveMes.dto.auth.UserInfoResponseDto;
import com.autoMotiveMes.entity.system.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 实现功能【用户表 mapper】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:46:14
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("""
          SELECT sp.perm_code
          FROM sys_user su
          LEFT JOIN sys_user_role sur ON su.user_id = sur.user_id
          LEFT JOIN sys_role_permission srp ON sur.role_id = srp.role_id
          LEFT JOIN sys_permission sp ON srp.perm_id = sp.perm_id
          WHERE su.username = #{username}
          """)
    List<String> selectUserPermissions(@Param("username") String username);

    @Select("SELECT r.role_code " +
            "FROM sys_user_role ur " +
            "JOIN sys_role r ON ur.role_id = r.role_id " +
            "WHERE ur.user_id = (SELECT user_id FROM sys_user WHERE username = #{username})")
    List<String> selectUserRoles(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    SysUser selectByEmail(@Param("email") String email);

    @Select("SELECT * FROM sys_user WHERE phone = #{phone}")
    SysUser selectByPhone(@Param("phone") String phone);

    @Select("SELECT u.user_id, u.username, u.real_name, r.role_id, u.dept_id, u.post_id, u.head_img, u.email, u.phone, u.status " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.user_id = ur.user_id " +
            "JOIN sys_role r ON ur.role_id = r.role_id " +
            "WHERE u.username = #{username}")
    UserInfoResponseDto getUserInfoByUsername(@Param("username") String username);

    Page<SysUser> selectUserList(Page<SysUser> page, @Param("query") SearchSysUserListRequestDto dto);
}
