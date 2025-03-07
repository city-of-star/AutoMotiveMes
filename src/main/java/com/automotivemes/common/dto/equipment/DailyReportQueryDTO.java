package com.automotivemes.common.dto.equipment;

import lombok.Data;

import java.util.Date;

@Data
public class DailyReportQueryDTO {
    private Date startDate;
    private Date endDate;
}
