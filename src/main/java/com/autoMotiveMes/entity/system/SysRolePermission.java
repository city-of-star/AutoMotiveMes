package com.autoMotiveMes.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 实现功能【角色权限关联实体类】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:28:53
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission {
    /**
     * 角色 ID，关联 sys_role 表的角色 ID
     */
    @TableId
    private Integer roleId;
    /**
     * 权限 ID，关联 sys_permission 表的权限 ID
     */
    @TableField
    private Integer permId;
}
