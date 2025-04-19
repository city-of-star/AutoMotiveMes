package com.autoMotiveMes.entity.equipment;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 实现功能【设备维护记录实体层】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:04:09
 */
@Data
@TableName("equipment_maintenance")
public class EquipmentMaintenance {
    /**
     * 维护记录ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long maintenanceId;

    /**
     * 设备ID，关联设备基础信息表
     */
    private Long equipmentId;

    /**
     * 计划维护日期，不能为空
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planDate;

    /**
     * 实际维护日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualDate;

    /**
     * 维护类型：1-日常保养 2-定期维护 3-紧急维修
     */
    private Integer maintenanceType;

    /**
     * 维护内容，不能为空
     */
    private String maintenanceContent;

    /**
     * 维护人员，不能为空
     */
    private String operator;

    /**
     * 维护结果
     */
    private String result;

    /**
     * 维护成本（精度：10位数字，2位小数）
     */
    private BigDecimal cost;
}
