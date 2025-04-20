package com.autoMotiveMes.dto.equipment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【报警历史查询出参】
 *
 * @author li.hongyu
 * @date 2025-04-19 23:10:26
 */
@Data
public class AlarmHistoryResponseDto {
    private Long alarmId;
    private String alarmCode;
    private String alarmReason;
    private Integer alarmLevel;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private Integer duration;
    private Integer status;
    private String handler;
    private String solution;

    private Long equipmentId;
    private String equipmentCode;
    private String equipmentName;
    private String location;
    private String typeName;
}