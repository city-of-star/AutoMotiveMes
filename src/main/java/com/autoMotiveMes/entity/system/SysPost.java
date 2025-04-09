package com.autoMotiveMes.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 实现功能【岗位表】
 *
 * @author li.hongyu
 * @date 2025-04-02 09:52:32
 */
@Data
@TableName("sys_post")
public class SysPost {
    /**
     * 岗位 ID，作为主键，采用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    private Long postId;
    /**
     * 岗位名称，不能为空
     */
    private String postName;
    /**
     * 岗位编码，唯一且不能为空
     */
    private String postCode;
    /**
     * 所属部门 ID
     */
    private Long deptId;
    /**
     * 显示顺序
     */
    private Integer orderNum;
    /**
     * 状态(0:停用 1:启用)
     */
    private Integer status;
    /**
     * 创建时间，默认值为记录插入时的当前时间
     */
    private Date createTime;
    /**
     * 更新时间，当记录更新时会自动更新该字段的值
     */
    private Date updateTime;
}