package com.autoMotiveMes.dto.order;

import lombok.Data;

/**
 * 实现功能【工单列表出参】
 *
 * @author li.hongyu
 * @date 2025-04-23 20:57:59
 */
@Data
public class ListWorkOrderResponseDto {
    private Long orderId;
    private String orderNo;
    private String equipmentCode;
    private String equipmentName;
    private String orderType;
    private String content;
    private String status;
    private String creatorName;
    private String assigneeName;
    private String createTime;
    private String finishTime;
    private Integer planCount;
}