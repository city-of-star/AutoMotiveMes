package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【产品基础信息实体类】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:45:12
 */
@Data
@TableName("product")
public class Product {
    /**
     * 产品ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long productId;

    /**
     * 产品型号，唯一且不能为空
     */
    private String productCode;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品规格参数（JSON格式）
     */
    private String specifications;

    /**
     * 标准节拍（秒/件）
     */
    private Integer standardCycleTime;

    /**
     * 安全库存量
     */
    private Integer safetyStock;

    /**
     * 创建时间，默认当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间，默认当前时间并在更新时自动刷新
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}