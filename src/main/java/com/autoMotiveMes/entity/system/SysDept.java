package com.autoMotiveMes.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【部门实体类】
 *
 * @author li.hongyu
 * @date 2025-04-02 09:50:25
 */
@Data
@TableName("sys_dept")
public class SysDept {
    /**
     * 部门 ID，作为主键，采用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    private Long deptId;
    /**
     * 部门名称，不能为空
     */
    private String deptName;
    /**
     * 父部门 ID（支持树形层级）
     */
    private Long parentId;
    /**
     * 显示顺序
     */
    private Integer orderNum;
    /**
     * 状态(0:停用 1:启用)
     */
    private Integer status;
    /**
     * 负责人 ID（关联用户）
     */
    private Long leaderId;
    /**
     * 创建时间，默认值为记录插入时的当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 更新时间，当记录更新时会自动更新该字段的值
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}