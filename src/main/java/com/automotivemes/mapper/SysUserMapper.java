package com.automotivemes.mapper;

import com.automotivemes.common.dto.UserInfoResponse;
import com.automotivemes.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(String username);

    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    SysUser selectByEmail(String email);

    @Select("SELECT real_name, email, phone FROM sys_user WHERE username = #{username}")
    UserInfoResponse getUserInfoByUsername(String username);
}
