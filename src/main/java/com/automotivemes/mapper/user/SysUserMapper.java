package com.automotivemes.mapper.user;

import com.automotivemes.common.dto.user.UserInfoResponseDto;
import com.automotivemes.entity.user.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("""
          SELECT
              sp.perm_code
          FROM
              sys_user su
          LEFT JOIN
              sys_user_role sur ON su.user_id = sur.user_id
          LEFT JOIN
              sys_role_permission srp ON sur.role_id = srp.role_id
          LEFT JOIN
              sys_permission sp ON srp.perm_id = sp.perm_id
          WHERE
              su.username = #{username}
          """)
    List<String> selectUserPermissions(String username);

    @Select("SELECT r.role_code " +
            "FROM sys_user_role ur " +
            "JOIN sys_role r ON ur.role_id = r.role_id " +
            "WHERE ur.user_id = (SELECT user_id FROM sys_user WHERE username = #{username})")
    List<String> selectUserRoles(String username);

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(String username);

    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    SysUser selectByEmail(String email);

    @Select("SELECT real_name, email, phone FROM sys_user WHERE username = #{username}")
    UserInfoResponseDto getUserInfoByUsername(String username);
}
