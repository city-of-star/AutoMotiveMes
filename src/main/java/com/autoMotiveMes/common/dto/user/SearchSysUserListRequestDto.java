package com.autoMotiveMes.common.dto.user;

import lombok.Data;

/**
 * 实现功能【查询用户列表入参】
 *
 * @author li.hongyu
 * @date 2025-04-01 22:54:41
 */
@Data
public class SearchSysUserListRequestDto {
    private Integer page;
    private Integer size;
    private Integer deptId;
    private String username;
    private String phone;
    private Integer status;
    private String startTime;
    private String endTime;
}