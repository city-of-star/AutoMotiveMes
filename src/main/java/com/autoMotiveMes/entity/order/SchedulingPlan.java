package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 实现功能【排程计划实体层】
 *
 * @author li.hongyu
 * @date 2025-04-23 21:03:26
 */
@Data
@TableName("scheduling_plan")
public class SchedulingPlan {
    /**
     * 计划ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long planId;

    /**
     * 关联工单ID
     */
    private Long orderId;

    /**
     * 设备ID
     */
    private Long equipmentId;

    /**
     * 计划日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planDate;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    /**
     * 进度百分比，默认0
     */
    private Integer progress;

    /**
     * 备注
     */
    private String remarks;
}