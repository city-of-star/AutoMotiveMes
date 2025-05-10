package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.*;
import com.autoMotiveMes.entity.system.SysUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 实现功能【用户管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-05 17:55:03
 */
public interface UserService {

    /**
     * 分页查询用户列表
     * @param dto 查询条件
     * @return 分页用户列表
     */
    Page<SysUser> getUserPage(GetUserPageDto dto);

    /**
     * 批量删除用户
     * @param dto 待删除的用户id数组
     */
    void deleteUser(DeleteUserDto dto);

    /**
     * 切换用户状态（启用/禁用）
     * @param dto 用户状态切换参数（用户ID）
     */
    void switchUserStatus(SwitchUserStatusDto dto);

    /**
     * 新增用户
     * @param dto 用户信息参数
     */
    void addUser(AddUserDto dto);

    /**
     * 更新用户信息
     * @param dto 用户更新参数
     */
    void updateUser(UpdateUserDto dto);

    /**
     * 获取用户详细信息(用于修改用户信息时获取用户原本信息)
     * @param dto 用户信息查询参数（用户ID）
     * @return 用户详细信息
     */
    GetUserInfoVo getUserInfo(GetUserInfoDto dto);

    /**
     * 重置用户密码
     * @param dto 密码重置参数（用户ID和新密码）
     */
    void resetPassword(ResetPasswordDto dto);

    /**
     * 获取用户导入模板
     * @param response http响应
     */
    void getUserImportTemplate(HttpServletResponse response);

    /**
     * 批量导入用户
     */
    void batchImportUser(MultipartFile file);

    /**
     * 导出用户
     * @param response http响应
     */
    void exportUser(HttpServletResponse response);
}