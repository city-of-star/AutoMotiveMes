package com.autoMotiveMes.service.business;

import com.autoMotiveMes.dto.order.DailyOrderProgressVo;
import com.autoMotiveMes.dto.order.DailyProductionDetailVo;
import com.autoMotiveMes.dto.order.DailyProductionSummaryVo;
import com.autoMotiveMes.dto.order.EquipmentDailyStatusVo;
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
    DailyProductionSummaryVo getDailySummary(LocalDate date);

    /**
     * 分页查询当日生产明细
     */
    Page<DailyProductionDetailVo> getDailyDetails(LocalDate date, Integer page, Integer size);

    /**
     * 获取工单进度列表
     */
    List<DailyOrderProgressVo> getOrderProgress(LocalDate date);

    /**
     * 获取设备状态统计
     */
    EquipmentDailyStatusVo getEquipmentStatusStats(LocalDate date);
}