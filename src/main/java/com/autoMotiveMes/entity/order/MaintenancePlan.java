package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
/**
 * 实现功能【维护计划实体类】
 *
 * @author li.hongyu
 * @date 2025-04-23 21:04:59
 */
@Data
@TableName("maintenance_plan")
public class MaintenancePlan {
    /**
     * 计划ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long planId;

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
     * 维护周期（天）
     */
    private Integer cycleDays;

    /**
     * 上次维护日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastDate;

    /**
     * 维护内容
     */
    private String content;
}