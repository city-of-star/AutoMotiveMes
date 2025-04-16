package com.autoMotiveMes.entity.equipment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实现功能【设备基础信息实体类】
 *
 * @author li.hongyu
 * @date 2025-04-15 22:36:25
 */
@Data
@TableName("equipment")
public class Equipment {
    /**
     * 设备唯一标识，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long equipmentId;

    /**
     * 设备编码（规则：车间代码+流水号），唯一且不能为空
     */
    private String equipmentCode;

    /**
     * 设备名称
     */
    private String equipmentName;

    /**
     * 设备型号
     */
    private String equipmentModel;

    /**
     * 设备类型（关联equipment_type.type_id）
     */
    private Integer equipmentType;

    /**
     * 安装位置（格式：车间/生产线/工位）
     */
    private String location;

    /**
     * 当前状态：1-正常 2-待机 3-维护中 4-已报废
     */
    private Integer status;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 生产日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    /**
     * 安装日期，不能为空
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate installationDate;

    /**
     * 上次维护日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastMaintenanceDate;

    /**
     * 维护周期（单位：天）
     */
    private Integer maintenanceCycle;

    /**
     * 创建时间，默认当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 最后更新时间，默认当前时间并在更新时自动刷新
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
