package com.autoMotiveMes.dto.system;

import lombok.Data;

import java.util.List;

/**
 * 实现功能【部门树节点】
 *
 * @author li.hongyu
 * @date 2025-04-05 18:17:07
 */
@Data
public class DeptTreeNodeVo {
    private Long deptId;
    private String deptName;
    private Long parentId;
    private Integer orderNum;
    private List<DeptTreeNodeVo> children;
}