package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【产品工序定义表实体类】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:44:23
 */
@Data
@TableName("process_definition")
public class ProcessDefinition {
    /**
     * 工序ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long processId;

    /**
     * 工序编码，唯一且不能为空
     */
    private String processCode;

    /**
     * 工序名称
     */
    private String processName;

    /**
     * 关联产品ID
     */
    private Long productId;

    /**
     * 工序顺序
     */
    private Integer sequence;

    /**
     * 适用设备类型
     */
    private Integer equipmentType;

    /**
     * 标准工时（秒）
     */
    private Integer standardTime;

    /**
     * 质量检测点配置（JSON格式）
     */
    private String qualityCheckpoints;

    /**
     * 前道工序ID
     */
    private Long previousProcess;

    /**
     * 创建时间，默认当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
