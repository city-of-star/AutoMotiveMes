package com.autoMotiveMes.dto.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:10:07
 */
@Data
public class DailyProductionDetailDto {
    private String orderNo;            // 工单号
    private String productCode;        // 产品型号
    private String processName;        // 工序名称
    private String equipmentCode;      // 设备编号
    private Integer outputQuantity;    // 产出数量
    private Integer defectiveQuantity;  // 不良数量
    private String operatorName;       // 操作员
    private LocalDateTime startTime;   // 开始时间
    private LocalDateTime endTime;     // 结束时间
    private String duration;           // 生产时长
}