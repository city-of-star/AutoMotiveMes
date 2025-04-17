package com.autoMotiveMes.dto.system;

import com.autoMotiveMes.entity.system.SysRole;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【获取角色列表出参】
 *
 * @author li.hongyu
 * @date 2025-04-11 09:52:01
 */
@Data
public class GetRoleListResponseDto {
    private List<SysRole> roles;
}