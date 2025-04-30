package com.autoMotiveMes.dto.system;

import lombok.Data;

/**
 * 实现功能【分页查询角色列表入参】
 *
 * @author li.hongyu
 * @date 2025-04-30 11:01:54
 */
@Data
public class GetRolePageDto {
    private Integer page;
    private Integer size;
    private String roleName;
    private String roleCode;
}