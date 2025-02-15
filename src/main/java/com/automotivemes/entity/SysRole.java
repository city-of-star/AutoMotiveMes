package com.automotivemes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统角色实体类，对应数据库中的 sys_role 表
 */
@Data
@TableName("sys_role")
public class SysRole {
    /**
     * 角色 ID，作为主键，采用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    private Integer roleId;
    /**
     * 角色编码，唯一且不能为空
     */
    private String roleCode;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色的描述信息
     */
    private String description;
}
