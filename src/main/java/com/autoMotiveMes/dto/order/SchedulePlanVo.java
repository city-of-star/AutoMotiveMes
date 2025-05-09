package com.autoMotiveMes.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【排程计划响应】
 *
 * @author li.hongyu
 * @date 2025-04-24 10:07:59
 */
@Data
public class SchedulePlanVo {
    private Long scheduleId;
    private String orderNo;
    private String processName;
    private String equipmentCode;
    private String equipmentName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime plannedStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime plannedEndTime;
    private Integer scheduleStatus;
    private String operatorName;
}