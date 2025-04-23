package com.autoMotiveMes.dto.order;

import lombok.Data;

/**
 * 实现功能【工单详情出参】
 *
 * @author li.hongyu
 * @date 2025-04-23 20:58:58
 */
@Data
public class WorkOrderDetailResponseDto {
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
    private String schedules;        // JSON格式排程计划
    private String maintenanceInfo;  // JSON格式维护计划
}