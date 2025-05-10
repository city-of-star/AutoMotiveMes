package com.autoMotiveMes.controller.system;

import com.autoMotiveMes.dto.system.*;
import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.entity.system.SysUser;
import com.autoMotiveMes.service.system.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 实现功能【用户管理服务 controller】
 *
 * @author li.hongyu
 * @date 2025-04-05 17:59:04
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     * @param dto 查询条件
     * @return 分页用户列表
     */
    @PostMapping("/page")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:list')")
    public R<Page<SysUser>> getUserPage(@RequestBody GetUserPageDto dto) {
        return R.success(userService.getUserPage(dto));
    }

    /**
     * 批量删除用户
     * @param dto 待删除的用户id数组
     * @return 操作结果（无具体数据返回）
     */
    @PostMapping("/delete")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:delete')")
    public R<?> deleteUser(@RequestBody DeleteUserDto dto) {
        userService.deleteUser(dto);
        return R.success();
    }

    /**
     * 切换用户状态（启用/禁用）
     * @param dto 用户状态切换参数（用户ID）
     * @return 操作结果（无具体数据返回）
     */
    @PostMapping("/switchStatus")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:manage')")
    public R<?> switchUserStatus(@RequestBody SwitchUserStatusDto dto) {
        userService.switchUserStatus(dto);
        return R.success();
    }

    /**
     * 新增用户
     * @param dto 用户信息参数
     * @return 操作结果（无具体数据返回）
     */
    @PostMapping("/add")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:add')")
    public R<?> addUser(@RequestBody AddUserDto dto) {
        userService.addUser(dto);
        return R.success();
    }

    /**
     * 更新用户信息
     * @param dto 用户更新参数
     * @return 操作结果（无具体数据返回）
     */
    @PostMapping("/update")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:update')")
    public R<?> updateUser(@RequestBody UpdateUserDto dto) {
        userService.updateUser(dto);
        return R.success();
    }

    /**
     * 获取用户详细信息
     * @param dto 用户信息查询参数（用户ID）
     * @return 用户详细信息
     */
    @PostMapping("/getInfo")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:manage')")
    public R<?> getUserInfo(@RequestBody GetUserInfoDto dto) {
        return R.success(userService.getUserInfo(dto));
    }

    /**
     * 重置用户密码
     * @param dto 密码重置参数（用户ID和新密码）
     * @return 操作结果（无具体数据返回）
     */
    @PostMapping("/resetPassword")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:manage')")
    public R<?> resetPassword(@RequestBody ResetPasswordDto dto) {
        userService.resetPassword(dto);
        return R.success();
    }

    /**
     * 获取用户导入模板
     */
    @GetMapping("/getUserImportTemplate")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:import')")
    public void getUserImportTemplate(HttpServletResponse response) {
        userService.getUserImportTemplate(response);
    }

    /**
     * 批量导入用户
     */
    @PostMapping("/batchImportUser")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:import')")
    public R<?> batchImportUser(MultipartFile file) {
        userService.batchImportUser(file);
        return R.success();
    }

    /**
     * 获取导出用户表格
     * @param response http响应
     */
    @GetMapping("/exportUser")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:user:export')")
    public void exportUser(HttpServletResponse response) {
        userService.exportUser(response);
    }
}