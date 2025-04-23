package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【工单实体层】
 *
 * @author li.hongyu
 * @date 2025-04-23 21:02:50
 */
@Data
@TableName("work_order")
public class WorkOrder {
    /**
     * 工单ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long orderId;

    /**
     * 工单编号（例：WO202312001），唯一且不能为空
     */
    private String orderNo;

    /**
     * 关联设备ID
     */
    private Long equipmentId;

    /**
     * 类型（生产/维护/维修）
     */
    private String orderType;

    /**
     * 工单内容
     */
    private String content;

    /**
     * 状态（待处理/进行中/已完成），默认待处理
     */
    private String status;

    /**
     * 创建人
     */
    private Long creatorId;

    /**
     * 处理人
     */
    private Long assigneeId;

    /**
     * 创建时间，默认当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;
}