package com.autoMotiveMes.dto.equipment;

import lombok.Data;

/**
 * 实现功能【维护记录列表入参】
 *
 * @author li.hongyu
 * @date 2025-04-21 20:29:53
 */
@Data
public class MaintenanceRecordListRequestDto {
    private Integer page;
    private Integer size;

    private String equipmentCode;     // 设备编码模糊查询
    private String equipmentName;     // 设备名称模糊查询
    private Integer maintenanceType;  // 维护类型数字（1日常保养/2定期维护/3紧急维修）
    private String planStartDate;     // 计划维护日期范围起 yyyy-MM-dd
    private String planEndDate;       // 计划维护日期范围止
    private String actualStartDate;   // 实际维护日期范围起
    private String actualEndDate;     // 实际维护日期范围止
    private String resultKeyword;     // 维护结果模糊关键字
}