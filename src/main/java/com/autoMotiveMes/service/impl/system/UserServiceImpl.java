package com.autoMotiveMes.service.impl.system;

import com.autoMotiveMes.dto.system.DeleteUserRequestDto;
import com.autoMotiveMes.dto.system.SearchSysUserListRequestDto;
import com.autoMotiveMes.common.exception.GlobalException;
import com.autoMotiveMes.dto.system.SwitchUserStatusRequestDto;
import com.autoMotiveMes.entity.system.SysUser;
import com.autoMotiveMes.mapper.system.SysUserMapper;
import com.autoMotiveMes.service.system.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 实现功能【用户管理服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-05 17:56:37
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;

    @Override
    public Page<SysUser> searchSysUserList(SearchSysUserListRequestDto dto) {
        // 定义返回值
        Page<SysUser> res;

        try {
            // 创建分页对象
            Page<SysUser> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                    dto.getSize() == null ? 10 : dto.getSize());

            res = userMapper.selectUserList(page, dto);
        } catch (Exception e) {
            log.error("查询用户列表出错: {}", e.getMessage());
            throw new GlobalException("查询用户列表出错: " + e.getMessage());
        }
        return res;
    }

    @Override
    public void deleteUserByID(DeleteUserRequestDto dto) {
        try {
            for (Long userId : dto.getUserIds()) {
                userMapper.deleteById(userId);
            }
        } catch (Exception e) {
            log.error("删除用户出错: {}", e.getMessage());
            throw new GlobalException("删除用户出错: " + e.getMessage());
        }
    }

    @Override
    public void switchUserStatus(SwitchUserStatusRequestDto dto) {
        try {
            SysUser user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                throw new GlobalException("修改失败，该用户不存在");
            } else if (user.getStatus() == 1) {
                user.setStatus(0);
            } else if (user.getStatus() == 0) {
                user.setStatus(1);
            } else {
                throw new GlobalException("修改失败，非法的用户状态");
            }
            userMapper.updateById(user);
        } catch (Exception e) {
            log.error("切换用户状态出错: {}", e.getMessage());
        }
    }
}