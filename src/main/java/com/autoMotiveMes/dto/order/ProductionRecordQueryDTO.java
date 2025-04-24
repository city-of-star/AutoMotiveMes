package com.autoMotiveMes.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【生产工单查询入参】
 *
 * @author li.hongyu
 * @date 2025-04-24 15:42:47
 */
@Data
public class ProductionRecordQueryDTO {
    // 基本筛选条件
    private String orderNo;          // 工单号模糊查询
    private String productName;      // 产品名称模糊查询
    private String productCode;      // 产品型号精确查询
    private String processName;      // 工序名称模糊查询
    private Long equipmentId;        // 设备ID精确查询
    private Long operatorId;         // 操作人员ID

    // 时间范围筛选
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeBegin; // 开始时间范围起
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeEnd;   // 开始时间范围止

    // 数量范围筛选
    private Integer minOutput;       // 最小产出数量
    private Integer maxOutput;       // 最大产出数量

    // 异常筛选
    private Boolean hasDefect;       // 是否包含不良品
    private Boolean hasRemark;       // 是否有异常备注

    // 分页排序
    private Integer page = 1;
    private Integer size = 10;
}