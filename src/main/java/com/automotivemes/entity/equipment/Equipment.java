package com.automotivemes.entity.equipment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 设备实体类，对应数据库中的 equipment 表
 */
@Data
@TableName("equipment")
public class Equipment {
    /**
     * 设备 ID，作为主键，采用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    private Integer equipmentId;
    /**
     * 设备名称，不能为空
     */
    private String equipmentName;
    /**
     * 设备类型（冲压/焊接/涂装/总装），不能为空
     */
    private String type;
    /**
     * 设备状态（运行/待机/故障/维护），默认为待机
     */
    private String status;
    /**
     * 设备安装位置
     */
    private String location;
    /**
     * 设备上线时间
     */
    private Date onlineTime;
    /**
     * 设备最后维护时间
     */
    private Date lastMaintenance;
}