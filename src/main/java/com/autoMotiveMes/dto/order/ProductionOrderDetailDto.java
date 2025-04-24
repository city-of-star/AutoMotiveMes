package com.autoMotiveMes.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【工单详情出参】
 *
 * @author li.hongyu
 * @date 2025-04-24 10:07:00
 */
@Data
public class ProductionOrderDetailDto {
    private Long orderId;
    private String orderNo;
    private String productCode;
    private String productName;
    private String specifications;
    private Integer orderQuantity;
    private Integer completedQuantity;
    private Integer defectiveQuantity;
    private Integer priority;
    private Integer status;
    private String plannedStartDate;
    private String plannedEndDate;
    private String actualStartDate;
    private String actualEndDate;
    private String productionLine;
    private String creatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}