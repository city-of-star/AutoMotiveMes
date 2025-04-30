package com.autoMotiveMes.dto.system;

import lombok.Data;

import java.util.List;

/**
 * 实现功能【新增角色入参】
 *
 * @author li.hongyu
 * @date 2025-04-30 15:18:54
 */
@Data
public class AddRoleDto {
    private String roleName;
    private String roleCode;
    private String description;
    private List<Integer> permIds;
}