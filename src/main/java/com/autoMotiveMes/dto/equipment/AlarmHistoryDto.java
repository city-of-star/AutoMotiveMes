package com.autoMotiveMes.dto.equipment;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 实现功能【报警历史查询入参】
 *
 * @author li.hongyu
 * @date 2025-04-19 23:11:37
 */
@Data
public class AlarmHistoryDto {
    private Integer page;
    private Integer size;
    private String equipmentCode;
    private Integer alarmLevel;
    private Integer status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String handler;
}