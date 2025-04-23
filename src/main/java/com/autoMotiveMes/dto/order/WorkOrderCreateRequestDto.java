package com.autoMotiveMes.dto.order;

import lombok.Data;

/**
 * 实现功能【工单创建入参】
 *
 * @author li.hongyu
 * @date 2025-04-23 20:59:39
 */
@Data
public class WorkOrderCreateRequestDto {
    private Long equipmentId;
    private String orderType;
    private String content;
    private Long assigneeId;
}