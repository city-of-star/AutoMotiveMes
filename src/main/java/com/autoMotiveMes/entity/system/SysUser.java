package com.autoMotiveMes.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【用户实体类】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:23:30
 */
@Data
@TableName("sys_user")
public class SysUser {
    /**
     * 用户 ID，作为主键，采用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    private Long userId;
    /**
     * 用户名，唯一且不能为空
     */
    private String username;
    /**
     * 加密后的用户密码
     */
    private String password;
    /**
     * 用户的真实姓名
     */
    private String realName;
    /**
     * 用户的性别
     */
    private String sex;
    /**
     * 用户的系统主题色
     */
    private String themeColor;
    /**
     * 用户所属的部门id
     */
    private Long deptId;
    /**
     * 用户所属的岗位id
     */
    private Long postId;
    /**
     * 用户所属的部门名称
     */
    @TableField(exist = false)
    private String deptName;
    /**
     * 用户的头像url
     */
    private String headImg;
    /**
     * 用户的邮箱地址
     */
    private String email;
    /**
     * 用户的联系电话
     */
    private String phone;
    /**
     * 用户状态，0 表示禁用，1 表示启用
     */
    private Integer status;
    /**
     * 用户是否锁定(0:已锁定;1:未锁定)
     */
    private Integer accountLocked;
    /**
     * 用户连续登录失败次数
     */
    private Integer loginAttempts;
    /**
     * 用户最后一次登录的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;
    /**
     * 用户记录创建的时间，默认值为记录插入时的当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 用户记录更新的时间，当记录更新时会自动更新该字段的值
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

