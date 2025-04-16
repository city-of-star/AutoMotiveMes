package com.autoMotiveMes.entity.equipment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【设备类型实体类】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:08:31
 */
@Data
@TableName("equipment_type")
public class EquipmentType {
    /**
     * 类型ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Integer typeId;

    /**
     * 类型名称（如：冲压机/焊接机器人），唯一且不能为空
     */
    private String typeName;

    /**
     * 类型描述
     */
    private String description;

    /**
     * 参数配置模板（JSON格式）
     */
    private String parametersConfig;

    /**
     * 创建时间，默认当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
