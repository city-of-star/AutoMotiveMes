package com.autoMotiveMes.dto.equipment;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【维护记录详情出参】
 *
 * @author li.hongyu
 * @date 2025-04-21 20:25:51
 */
@Data
public class MaintenanceRecordDetailVo {
    // 维护基础信息
    private Long maintenanceId;
    private String maintenanceType;
    private String planDate;
    private String actualDate;
    private String maintenanceContent; // 完整维护内容
    private String operator;
    private String result;             // 完整维护结果
    private BigDecimal cost;

    // 设备关联信息
    private String equipmentCode;
    private String equipmentName;
    private String equipmentModel;    // 设备型号（如HFP-200）
    private String location;           // 安装位置（车间/产线结构）
    private String manufacturer;       // 设备制造商

    // 维护周期信息
    private Integer maintenanceCycle;  // 维护周期（天）
    private String lastMaintenanceDate;// 上次维护日期（来自设备表）
}