package com.automotivemes.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统角色权限关联实体类，对应数据库中的 sys_role_permission 表，用于维护角色和权限的多对多关系
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
