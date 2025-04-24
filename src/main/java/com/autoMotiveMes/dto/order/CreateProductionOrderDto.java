package com.autoMotiveMes.dto.order;

import lombok.Data;

/**
 * 实现功能【创建工单入参】
 *
 * @author li.hongyu
 * @date 2025-04-24 10:07:29
 */
@Data
public class CreateProductionOrderDto {
    private Long productId;
    private Integer orderQuantity;
    private Integer priority;
    private String plannedStartDate;
    private String plannedEndDate;
    private String productionLine;
}