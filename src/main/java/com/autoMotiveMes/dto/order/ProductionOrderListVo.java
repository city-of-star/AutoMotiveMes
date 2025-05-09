package com.autoMotiveMes.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【工单列表查询出参】
 *
 * @author li.hongyu
 * @date 2025-04-24 10:06:30
 */
@Data
public class ProductionOrderListVo {
    private Long orderId;
    private String orderNo;
    private String productCode;
    private String productName;
    private Integer orderQuantity;
    private Integer completedQuantity;
    private Integer defectiveQuantity;
    private Integer priority;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String plannedStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String plannedEndDate;
    private String productionLine;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}