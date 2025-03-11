package com.automotivemes.common.dto.equipment;

import lombok.Data;

import java.util.Date;

@Data
public class AlarmStatisticsDTO {
    private Integer equipmentId;
    private Integer totalAlarms;
    private Date lastAlarmTime;
}
