package com.automotivemes.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 实现功能【用户角色关联实体类】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:27:04
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
