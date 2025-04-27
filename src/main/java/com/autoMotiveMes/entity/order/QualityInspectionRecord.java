package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【质量检测记录表实体类】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:46:45
 */
@Data
@TableName("quality_inspection_record")
public class QualityInspectionRecord {
    /**
     * 质检记录ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long inspectionId;

    /**
     * 工单ID
     */
    private Long orderId;

    /**
     * 质检项ID
     */
    private Long itemId;

    /**
     * 生产记录ID
     */
    private Long recordId;

    /**
     * 检测结果：1-合格 2-不合格 3-待复检
     */
    private Integer inspectionResult;

    /**
     * 检测数据记录（JSON格式）
     */
    private String inspectionData;

    /**
     * 检验员
     */
    private Long inspector;

    /**
     * 检验时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inspectionTime;

    /**
     * 备注说明
     */
    private String remark;
}
