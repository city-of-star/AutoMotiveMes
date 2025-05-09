package com.autoMotiveMes.dto.system;

import lombok.Data;

import java.util.List;

/**
 * 实现功能【权限树节点】
 *
 * @author li.hongyu
 * @date 2025-04-30 14:22:13
 */
@Data
public class SysPermissionTreeNodeVo {
    private Integer permId;
    private String permCode;
    private String permName;
    private String permType;
    private Integer parentId;
    private String path;
    private String component;
    private String icon;
    private Integer orderNum;
    private String apiPath;
    private String method;
    private List<SysPermissionTreeNodeVo> children;
}