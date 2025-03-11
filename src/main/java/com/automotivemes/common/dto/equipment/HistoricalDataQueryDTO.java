package com.automotivemes.common.dto.equipment;

import lombok.Data;

import java.util.Date;

@Data
public class HistoricalDataQueryDTO {
    private Integer equipmentId;
    private Date startDate;
    private Date endDate;

}
