package com.autoMotiveMes.dto.equipment;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【维护记录列表出参】
 *
 * @author li.hongyu
 * @date 2025-04-21 20:25:05
 */
@Data
public class MaintenanceRecordListResponseDto {
    private Long maintenanceId;       // 维护记录ID
    private String equipmentCode;     // 设备编码（如WSH-001）
    private String equipmentName;     // 设备名称（如数控冲压机）
    private String maintenanceType;   // 维护类型（中文：日常保养/定期维护/紧急维修）
    private String planDate;          // 计划维护日期（yyyy-MM-dd）
    private String actualDate;        // 实际维护日期（允许为空）
    private String operator;          // 维护人员
    private String result;            // 维护结果
    private BigDecimal cost;          // 维护成本
}