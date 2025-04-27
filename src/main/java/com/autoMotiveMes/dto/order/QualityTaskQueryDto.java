package com.autoMotiveMes.dto.order;

import lombok.Data;

/**
 * 实现功能【质检任务查询DTO】
 *
 * @author li.hongyu
 * @date 2025-04-27 14:57:43
 */
@Data
public class QualityTaskQueryDto {
    private String orderNo;       // 工单号模糊查询
    private String productName;   // 产品名称模糊查询
    private String processName;   // 工序名称精确查询
    private String inspectionItem;// 质检项名称模糊查询
    private Integer taskStatus;   // 任务状态（1-待检验 2-检验中 3-已完成）
    private Integer page = 1;     // 页码，默认第1页
    private Integer size = 10;    // 每页条数，默认10条
}