package com.automotivemes.common.dto.equipment;

import lombok.Data;

import java.util.Date;

@Data
public class DailyReportDTO {
    private Date date;
    private Integer equipmentId;
    private Integer totalRecords;
    private Double avgTemp;
    private Double maxTemp;
    private Integer alarmCount;
    private Integer runningCount;
}
