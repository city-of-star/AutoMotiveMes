package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【生产执行记录表实体类】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:47:04
 */
@Data
@TableName("production_record")
public class ProductionRecord {
    /**
     * 记录ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /**
     * 工单ID
     */
    private Long orderId;

    /**
     * 工序ID
     */
    private Long processId;

    /**
     * 设备ID
     */
    private Long equipmentId;

    /**
     * 产出数量
     */
    private Integer outputQuantity;

    /**
     * 不良数量
     */
    private Integer defectiveQuantity;

    /**
     * 质检任务生成标记：0-未生成 1-已生成
     */
    private Integer qualityCheckGenerated;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 操作员
     */
    private Long operator;

    /**
     * 异常备注
     */
    private String remark;
}
