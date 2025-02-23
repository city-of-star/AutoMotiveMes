package com.automotivemes.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统用户角色关联实体类，对应数据库中的 sys_user_role 表，用于维护用户和角色的多对多关系
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {
    /**
     * 用户 ID，关联 sys_user 表的用户 ID
     */
    @TableId
    private Long userId;
    /**
     * 角色 ID，关联 sys_role 表的角色 ID
     */
    @TableField
    private Integer roleId;
}
