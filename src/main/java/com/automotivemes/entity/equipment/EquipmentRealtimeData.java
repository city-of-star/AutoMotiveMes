package com.automotivemes.entity.equipment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 设备实时数据实体类，对应数据库中的 equipment_realtime_data 表
 */
@Data
@TableName("equipment_realtime_data")
public class EquipmentRealtimeData {
    /**
     * 数据记录 ID，作为主键，采用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    private Long equipmentRealtimeDataId;
    /**
     * 设备 ID，关联 equipment 表的 id 字段
     */
    private Integer equipmentId;
    /**
     * 时间戳，记录数据的采集时间
     */
    private Date timestamp;
    /**
     * 设备状态（运行/待机/故障/维护）
     */
    private String status;
    /**
     * 设备温度（℃）
     */
    private Double temperature;
    /**
     * 设备转速（RPM）
     */
    private Integer rpm;
    /**
     * 报警状态，0 表示正常，1 表示报警
     */
    private Boolean isAlarm;
}