package com.autoMotiveMes.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 实现功能【角色实体类】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:24:31
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
