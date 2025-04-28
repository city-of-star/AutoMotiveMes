package com.autoMotiveMes.service.business;

import com.autoMotiveMes.dto.order.QualityTaskDto;
import com.autoMotiveMes.dto.order.QualityTaskQueryDto;
import com.autoMotiveMes.dto.order.SubmitQualityResultDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 实现功能【质检服务】
 *
 * @author li.hongyu
 * @date 2025-04-28 13:56:17
 */
public interface QualityInspectionService {

    /**
     * 分页查询质检任务列表
     * @param dto 查询条件
     * @return 分页质检任务列表
     */
    Page<QualityTaskDto> listQualityTasks(QualityTaskQueryDto dto);

    /**
     * 生成质检任务
     * @param recordId 产品生产记录
     */
    void generateQualityTasks(Long recordId);

    /**
     * 提交质检结果
     * @param dto 质检结果
     */
    void submitQualityResult(SubmitQualityResultDto dto);
}