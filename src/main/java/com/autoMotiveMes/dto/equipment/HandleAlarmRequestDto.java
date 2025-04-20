package com.autoMotiveMes.dto.equipment;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【处理设备报警入参】
 *
 * @author li.hongyu
 * @date 2025-04-19 17:48:49
 */
@Data
public class HandleAlarmRequestDto {
    private Long alarmId;
    private String maintenanceContent;
    private String result;
    private BigDecimal cost;
}