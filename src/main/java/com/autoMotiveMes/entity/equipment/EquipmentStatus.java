package com.autoMotiveMes.entity.equipment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【设备状态记录实体类】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:07:41
 */
@Data
@TableName("equipment_status")
public class EquipmentStatus {
    /**
     * 状态记录ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long statusId;

    /**
     * 设备ID，关联设备基础信息表
     */
    private Long equipmentId;

    /**
     * 状态编码（1-运行 2-空闲 3-故障）
     */
    private Integer statusCode;

    /**
     * 状态详细描述
     */
    private String statusDetail;

    /**
     * 状态开始时间，不能为空
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 状态结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 持续时间（秒）
     */
    private Integer duration;
}
