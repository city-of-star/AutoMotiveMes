package com.autoMotiveMes.service.system;

import com.autoMotiveMes.common.dto.user.SearchSysUserListRequestDto;
import com.autoMotiveMes.entity.user.SysUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 实现功能【用户管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-05 17:55:03
 */
public interface UserService {

    Page<SysUser> searchSysUserList(SearchSysUserListRequestDto searchSysUserListRequestDto);

}