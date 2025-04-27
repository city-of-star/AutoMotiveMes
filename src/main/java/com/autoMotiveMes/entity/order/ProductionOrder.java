package com.autoMotiveMes.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实现功能【生产工单主表实体类】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:45:41
 */
@Data
@TableName("production_order")
public class ProductionOrder {
    /**
     * 工单ID，主键，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long orderId;

    /**
     * 工单号（规则：YYMMDD+4位流水），唯一且不能为空
     */
    private String orderNo;

    /**
     * 原工单ID（返工专用）
     */
    private Long reworkOf;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 计划数量
     */
    private Integer orderQuantity;

    /**
     * 已完成数量
     */
    private Integer completedQuantity;

    /**
     * 不良品数量
     */
    private Integer defectiveQuantity;

    /**
     * 优先级：1-紧急 2-高 3-普通 4-低
     */
    private Integer priority;

    /**
     * 状态：1-待排程 2-已排程 3-生产中 4-暂停 5-已完成 6-已关闭
     */
    private Integer status;

    /**
     * 计划开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate plannedStartDate;

    /**
     * 计划完成日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate plannedEndDate;

    /**
     * 实际开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualStartDate;

    /**
     * 实际完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualEndDate;

    /**
     * 生产线代码
     */
    private String productionLine;

    /**
     * 创建人
     */
    private Long creator;

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
