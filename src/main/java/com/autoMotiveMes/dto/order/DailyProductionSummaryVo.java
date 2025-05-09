package com.autoMotiveMes.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【日报页产品总结出参】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:09:44
 */
@Data
public class DailyProductionSummaryVo {
    private LocalDate reportDate;       // 报告日期
    private Integer totalOutput;        // 总产量
    private Integer qualifiedProducts;  // 合格品数量
    private BigDecimal qualifiedRate;   // 合格率
    private Integer equipmentUtilization; // 设备利用率（百分比）
    private Integer abnormalRecords;    // 异常记录数
}