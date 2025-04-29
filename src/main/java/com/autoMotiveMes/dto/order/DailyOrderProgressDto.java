package com.autoMotiveMes.dto.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:10:33
 */
@Data
public class DailyOrderProgressDto {
    private String orderNo;
    private String productName;
    private Integer totalQuantity;     // 工单总量
    private Integer completedQuantity;  // 已完成量
    private BigDecimal progressRate;   // 进度百分比
    private String currentProcess;     // 当前工序
}