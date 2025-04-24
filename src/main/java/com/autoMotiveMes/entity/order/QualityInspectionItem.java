package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【质量检测项目表实体类】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:46:21
 */
@Data
@TableName("quality_inspection_item")
public class QualityInspectionItem {
    /**
     * 质检项ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long itemId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 检测项目名称
     */
    private String inspectionName;

    /**
     * 检测标准
     */
    private String inspectionStandard;

    /**
     * 抽检方式（全检/抽检）
     */
    private String samplingMethod;

    /**
     * 合格标准（JSON格式）
     */
    private String acceptanceCriteria;

    /**
     * 创建时间，默认当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
