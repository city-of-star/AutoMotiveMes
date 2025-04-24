package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【生产排程计划表实体类】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:47:54
 */
@Data
@TableName("production_schedule")
public class ProductionSchedule {
    /**
     * 排程ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long scheduleId;

    /**
     * 工单ID
     */
    private Long orderId;

    /**
     * 工序ID
     */
    private Long processId;

    /**
     * 设备ID
     */
    private Long equipmentId;

    /**
     * 计划开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedStartTime;

    /**
     * 计划结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedEndTime;

    /**
     * 实际开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualStartTime;

    /**
     * 实际结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualEndTime;

    /**
     * 状态：1-待执行 2-执行中 3-已完成 4-已延迟
     */
    private Integer scheduleStatus;

    /**
     * 操作人员
     */
    private Long operator;
}
