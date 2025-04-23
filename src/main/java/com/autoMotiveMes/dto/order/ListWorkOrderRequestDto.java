package com.autoMotiveMes.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【工单列表入参】
 *
 * @author li.hongyu
 * @date 2025-04-23 20:56:26
 */
@Data
public class ListWorkOrderRequestDto {
    private Integer size;
    private Integer page;
    private String orderNo;       // 工单编号
    private String equipmentCode; // 设备编码
    private String status;        // 工单状态
    private String orderType;     // 工单类型
    private String creatorName;   // 创建人姓名
    private String assigneeName;  // 处理人姓名
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart; // 创建时间范围起
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;   // 创建时间范围止
}