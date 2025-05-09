package com.autoMotiveMes.dto.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【质检任务】
 *
 * @author li.hongyu
 * @date 2025-04-27 14:56:00
 */
@Data
public class QualityTaskDto {
    private Long taskId;
    private String orderNo;
    private String productName;
    private String processName;
    private String inspectionItems;  // 质检项名称
    private Integer taskStatus;  // 1-待检验 2-检验中 3-已完成
    private LocalDateTime inspectionData;
}