package com.autoMotiveMes.service.impl.system;

import com.autoMotiveMes.common.exception.BusinessException;
import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.dto.system.*;
import com.autoMotiveMes.common.exception.ServerException;
import com.autoMotiveMes.entity.system.SysUser;
import com.autoMotiveMes.entity.system.SysUserRole;
import com.autoMotiveMes.mapper.system.SysUserMapper;
import com.autoMotiveMes.mapper.system.SysUserRoleMapper;
import com.autoMotiveMes.service.system.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    private final RedisTemplate<String, SysUser> userRedisTemplate;
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<SysUser> searchSysUserList(SearchSysUserListRequestDto dto) {
        try {
            // 创建分页对象
            Page<SysUser> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                    dto.getSize() == null ? 10 : dto.getSize());

            return userMapper.selectUserList(page, dto);
        } catch (Exception e) {
            throw new ServerException("查询用户列表出错 || " + e.getMessage());
        }
    }

    @Override
    public void deleteUserByID(DeleteUserRequestDto dto) {
        try {
            for (Long userId : dto.getUserIds()) {
                userRoleMapper.deleteByUserId(userId);
                userMapper.deleteById(userId);
            }
        } catch (Exception e) {
            throw new ServerException("删除用户出错 || " + e.getMessage());
        }
    }

    @Override
    public void switchUserStatus(SwitchUserStatusRequestDto dto) {
        try {
            SysUser user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                throw new ServerException("修改失败，该用户不存在");
            } else if (user.getStatus() == 1) {
                user.setStatus(0);
            } else if (user.getStatus() == 0) {
                user.setStatus(1);
            } else {
                throw new ServerException("修改失败，非法的用户状态");
            }
            userMapper.updateById(user);

            // 清除旧缓存
            userRedisTemplate.delete("user:" + user.getUsername());
        } catch (Exception e) {
            throw new ServerException("切换用户状态出错 || " + e.getMessage());
        }
    }

    @Override
    public void addUser(AddUserRequestDto dto) {
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        if (userMapper.selectByPhone(dto.getPhone()) != null) {
            throw new BusinessException(ErrorCode.PHONE_EXISTS);
        }
        if (userMapper.selectByEmail(dto.getEmail()) != null) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }

        try {
            // 存储用户信息
            SysUser user = new SysUser();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setRealName(dto.getRealName());
            user.setHeadImg("https://img0.baidu.com/it/u=3440970623,2745306240&fm=253&fmt=auto&app=138&f=JPEG?w=260&h=260");  // 默认网图
            user.setStatus(dto.getStatus());
            user.setAccountLocked(1);  //  默认未锁定
            user.setLoginAttempts(0);  // 默认连续登陆失败次数为0
            user.setDeptId(dto.getDeptId());
            user.setPostId(dto.getPostId());
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);

            // 存储用户角色信息
            Long userId = userMapper.selectByUsername(dto.getUsername()).getUserId();  // 获取userId
            SysUserRole userRole = new SysUserRole(userId, dto.getRoleId());
            userRoleMapper.insert(userRole);  // 添加用户角色关系
        } catch (Exception e) {
            throw new ServerException("注册失败 || " +  e.getMessage());
        }
    }

    @Override
    public void updateUser(UpdateUserRequestDto dto) {
        SysUser user = userMapper.selectById(dto.getUserId());  // 原本的用户信息
        SysUser toUserByPhone = userMapper.selectByPhone(dto.getPhone());  // 用户改手机号码，查询是否已有该手机号码
        if (toUserByPhone != null && !toUserByPhone.getPhone().equals(user.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_EXISTS);
        }
        SysUser toUserByEmail = userMapper.selectByEmail(dto.getEmail());  // 用户改邮箱，查询是否已有该邮箱
        if (toUserByEmail != null && !toUserByEmail.getEmail().equals(user.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }

        try {
            Long userId = user.getUserId();  // 获取userId

            // 修改用户信息
            SysUser newUser = new SysUser();
            newUser.setUserId(userId);
            newUser.setPhone(dto.getPhone());
            newUser.setEmail(dto.getEmail());
            newUser.setRealName(dto.getRealName());
            newUser.setStatus(dto.getStatus());
            newUser.setDeptId(dto.getDeptId());
            newUser.setPostId(dto.getPostId());
            userMapper.updateById(newUser);

            // 修改用户的角色信息
            userRoleMapper.deleteByUserId(userId);  // 删除用户原本的角色
            SysUserRole userRole = new SysUserRole(userId, dto.getRoleId());
            userRoleMapper.insert(userRole);  // 添加新的用户角色关系

            // 清除旧缓存
            SysUser updatedUser = userMapper.selectById(userId);
            userRedisTemplate.delete("user:" + updatedUser.getUsername());
        } catch (Exception e) {
            throw new ServerException("修改失败 || " +  e.getMessage());
        }
    }

    @Override
    public GetUserInfoResponseDto getUserInfo(GetUserInfoRequestDto dto) {

        GetUserInfoResponseDto res = new GetUserInfoResponseDto();
        try {
            SysUser user = userMapper.selectById(dto.getUserId());
            res.setUsername(user.getUsername());
            res.setRealName(user.getRealName());
            res.setHeadImg(user.getHeadImg());
            res.setPhone(user.getPhone());
            res.setEmail(user.getEmail());
            res.setUserId(user.getUserId());
            res.setDeptId(user.getDeptId());
            res.setPostId(user.getPostId());
            res.setStatus(user.getStatus());
            SysUserRole userRole = userRoleMapper.selectByUserId(user.getUserId());
            res.setRoleId(userRole.getRoleId());
        } catch (Exception e) {
            throw new ServerException("获取用户信息出错" + e.getMessage());
        }
        return res;
    }
}