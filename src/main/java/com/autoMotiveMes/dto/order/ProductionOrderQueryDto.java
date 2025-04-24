package com.autoMotiveMes.dto.order;

import lombok.Data;

/**
 * 实现功能【工单查询请求入参】
 *
 * @author li.hongyu
 * @date 2025-04-24 10:06:01
 */
@Data
public class ProductionOrderQueryDto {
    private Integer page;
    private Integer size;
    private String orderNo;       // 工单号模糊查询
    private String productCode;   // 产品型号
    private Integer status;        // 工单状态
    private String startDate;     // 计划开始日期范围查询
    private String endDate;
}