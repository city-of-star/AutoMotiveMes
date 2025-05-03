package com.autoMotiveMes.mapper.system;

import com.autoMotiveMes.dto.system.GetUserPageDto;
import com.autoMotiveMes.dto.auth.UserInfoResponseDto;
import com.autoMotiveMes.entity.system.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【用户表 mapper】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:46:14
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    // 通过用户名获取用户权限编码
    List<String> selectUserPermissionsByUsername(@Param("username") String username);

    // 通过用户名获取用户角色编码
    List<String> selectUserRolesByUsername(@Param("username") String username);

    // 通过用户名获取用户
    SysUser selectByUsername(@Param("username") String username);

    // 通过邮箱获取用户
    SysUser selectByEmail(@Param("email") String email);

    // 通过手机号码获取用户
    SysUser selectByPhone(@Param("phone") String phone);

    // 通过用户名获取用户详细信息
    UserInfoResponseDto getUserInfoByUsername(@Param("username") String username);

    // 获取用户列表
    Page<SysUser> selectUserList(Page<SysUser> page, @Param("query") GetUserPageDto dto);
}
