package com.autoMotiveMes.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实现功能【生产工单查询出参】
 *
 * @author li.hongyu
 * @date 2025-04-24 15:44:05
 */
@Data
public class ProductionRecordVo {
    // 记录基础信息
    private Long recordId;           // 记录ID
    private String equipmentCode;    // 设备编号（需关联设备表）

    // 工单关联信息
    private String orderNo;          // 工单号
    private String productName;      // 产品名称
    private String productCode;      // 产品型号
    private Integer orderQuantity;    // 工单计划数量
    private Integer orderCompleted;  // 工单已完成数量

    // 工序信息
    private String processCode;      // 工序编码
    private String processName;      // 工序名称
    private Integer processSequence; // 工序顺序

    // 生产数据
    private Integer outputQuantity;  // 产出数量
    private Integer defectiveQuantity; // 不良数量
    private BigDecimal defectRate;   // 不良率（计算字段）

    // 时间信息
    private LocalDateTime startTime; // 实际开始时间
    private LocalDateTime endTime;   // 实际结束时间
    private Long duration;       // 生产耗时（计算字段）

    // 人员信息
    private String operatorName;     // 操作员姓名（需关联用户表）

    // 质量关联
    private Integer inspectionResult;// 最新质检结果（1合格 2不合格）
    private String qualityStatus;    // 质检状态显示

    // 异常信息
    private String remark;           // 异常备注
    private Boolean hasAbnormal;     // 是否异常标记

    // 展示用格式化字段
    private String timeRange;        // 格式化显示的时间范围
    private String durationDisplay;  // 格式化的耗时显示
}