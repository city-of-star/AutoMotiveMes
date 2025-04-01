package com.automotivemes.common.dto.user;

import lombok.Data;

/**
 * 实现功能【获取用户列表入参】
 *
 * @author li.hongyu
 * @date 2025-04-01 22:54:41
 */
@Data
public class ListSysUserRequestDto {
    private Integer page;
    private Integer size;
}