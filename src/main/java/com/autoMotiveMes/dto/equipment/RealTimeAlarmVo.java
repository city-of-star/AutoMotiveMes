package com.autoMotiveMes.dto.equipment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实现功能【获取实时报警信息出参】
 *
 * @author li.hongyu
 * @date 2025-04-19 18:08:18
 */
@Data
public class RealTimeAlarmVo {
    private Long alarmId;
    private String alarmCode;
    private String alarmReason;
    private Integer alarmLevel;
    private LocalDateTime startTime;
    private Integer alarmStatus;

    private Long equipmentId;
    private String equipmentCode;
    private String equipmentName;
    private String equipmentModel;
    private String location;
    private Integer equipmentStatus;
    private String manufacturer;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate installationDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastMaintenanceDate;
    private Integer maintenanceCycle;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String typeName;
    private String description;
    private String parametersConfig;

}