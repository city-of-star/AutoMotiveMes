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
    // 通过用户名获取用户权限编码
    List<String> selectUserPermissionsByUsername(@Param("username") String username);

    @Select("SELECT r.role_code " +
            "FROM sys_user_role ur " +
            "JOIN sys_role r ON ur.role_id = r.role_id " +
            "WHERE ur.user_id = (SELECT user_id FROM sys_user WHERE username = #{username})")
    // 通过用户名获取用户角色编码
    List<String> selectUserRolesByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    // 通过用户名获取用户
    SysUser selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    // 通过邮箱获取用户
    SysUser selectByEmail(@Param("email") String email);

    @Select("SELECT * FROM sys_user WHERE phone = #{phone}")
    // 通过手机号码获取用户
    SysUser selectByPhone(@Param("phone") String phone);

    @Select("SELECT u.user_id, u.username, u.real_name, r.role_name, u.sex, u.theme_color, d.dept_name, p.post_name, u.head_img, u.email, u.phone, u.create_time " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.user_id = ur.user_id " +
            "JOIN sys_role r ON ur.role_id = r.role_id " +
            "JOIN sys_dept d ON u.dept_id = d.dept_id " +
            "JOIN sys_post p ON u.post_id = p.post_id " +
            "WHERE u.username = #{username}")
    // 通过用户名获取用户详细信息
    UserInfoResponseDto getUserInfoByUsername(@Param("username") String username);

    // 获取用户列表
    Page<SysUser> selectUserList(Page<SysUser> page, @Param("query") SearchSysUserListRequestDto dto);
}
