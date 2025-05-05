package com.autoMotiveMes.service.business;

import com.autoMotiveMes.dto.order.DailyOrderProgressDto;
import com.autoMotiveMes.dto.order.DailyProductionDetailDto;
import com.autoMotiveMes.dto.order.DailyProductionSummaryDto;
import com.autoMotiveMes.dto.order.EquipmentDailyStatusDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDate;
import java.util.List;

/**
 * 实现功能【生产日报接口】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:09:16
 */
public interface DailyReportService {
    /**
     * 获取每日生产概况统计
     */
    DailyProductionSummaryDto getDailySummary(LocalDate date);

    /**
     * 分页查询当日生产明细
     */
    Page<DailyProductionDetailDto> getDailyDetails(LocalDate date, Integer page, Integer size);

    /**
     * 获取工单进度列表
     */
    List<DailyOrderProgressDto> getOrderProgress(LocalDate date);

    /**
     * 获取设备状态统计
     */
    EquipmentDailyStatusDto getEquipmentStatusStats(LocalDate date);
}