package com.autoMotiveMes.dto.system;

import lombok.Data;

import java.util.List;

/**
 * 实现功能【修改角色入参】
 *
 * @author li.hongyu
 * @date 2025-04-30 16:21:50
 */
@Data
public class UpdateRoleDto {
    private Integer roleId;
    private String roleName;
    private String roleCode;
    private String description;
    private List<Integer> permIds;
}