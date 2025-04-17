package com.autoMotiveMes.entity.equipment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【设备运行参数记录实体类】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:05:50
 */
@Data
@TableName("equipment_parameters")
public class EquipmentParameters {
    /**
     * 参数记录ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long paramId;

    /**
     * 设备ID，关联设备基础信息表
     */
    private Long equipmentId;

    /**
     * 参数名称（如：温度/压力），不能为空
     */
    private String paramName;

    /**
     * 参数数值（精度：12位数字，4位小数）
     */
    private Double paramValue;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 采集时间，不能为空
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime collectTime;

    /**
     * 是否正常：0-异常 1-正常
     */
    private Integer isNormal;
}
