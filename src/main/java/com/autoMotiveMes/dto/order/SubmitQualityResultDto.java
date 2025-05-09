package com.autoMotiveMes.dto.order;

import lombok.Data;

/**
 * 实现功能【质检结果提交入参】
 *
 * @author li.hongyu
 * @date 2025-04-27 14:56:28
 */
@Data
public class SubmitQualityResultDto {
    private Long recordId;
    private Integer inspectionResult;
    private String inspectionData;
    private String remark;
}