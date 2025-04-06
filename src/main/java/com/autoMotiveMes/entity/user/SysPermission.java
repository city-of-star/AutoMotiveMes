package com.autoMotiveMes.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 实现功能【权限实体类】
 *
 * @author li.hongyu
 * @date 2025-02-15 15:25:13
 */
@Data
@TableName("sys_permission")
public class SysPermission {
    /**
     * 权限 ID，作为主键，采用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    private Integer permId;
    /**
     * 权限编码，唯一且不能为空
     */
    private String permCode;
    /**
     * 权限名称
     */
    private String permName;
    /**
     * 权限类型，包括 'MENU'（菜单权限）、'BUTTON'（按钮权限）、'API'（接口权限）
     */
    private String permType;
    /**
     * 父权限的 ID，默认为 0 表示顶级权限
     */
    private Integer parentId;
    /**
     * 前端路由路径，用于页面导航
     */
    private String path;
    /**
     * 前端组件名称，对应页面展示的组件
     */
    private String component;
    /**
     * 菜单图标，用于菜单展示的图标标识
     */
    private String icon;
    /**
     * 权限的显示顺序，数值越小越靠前
     */
    private Integer orderNum;
    /**
     * 后端接口路径，对应权限所关联的后端 API 地址
     */
    private String apiPath;
    /**
     * 访问后端接口的请求方法，如 POST、GET 等
     */
    private String method;
}
