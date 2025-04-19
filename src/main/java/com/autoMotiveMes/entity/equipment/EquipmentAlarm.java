package com.autoMotiveMes.entity.equipment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * 实现功能【设备报警记录实体类】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:02:18
 */
@Data
@TableName("equipment_alarm")
public class EquipmentAlarm {
    /**
     * 报警记录ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long alarmId;

    /**
     * 设备ID，关联设备基础信息表
     */
    private Long equipmentId;

    /**
     * 报警编码（按标准编码规则），不能为空
     */
    private String alarmCode;

    /**
     * 报警原因
     */
    private String alarmReason;

    /**
     * 报警等级：1-警告 2-一般故障 3-严重故障
     */
    private Integer alarmLevel;

    /**
     * 报警开始时间，不能为空
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 报警解除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 持续时间（秒）
     */
    private Integer duration;

    /**
     * 处理状态：0-未处理 1-处理中 2-已处理
     */
    private Integer status;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理方案
     */
    private String solution;
}
