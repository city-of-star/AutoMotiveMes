package com.autoMotiveMes.service.impl.business;

import com.autoMotiveMes.dto.order.DailyOrderProgressDto;
import com.autoMotiveMes.dto.order.DailyProductionDetailDto;
import com.autoMotiveMes.dto.order.DailyProductionSummaryDto;
import com.autoMotiveMes.dto.order.EquipmentDailyStatusDto;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.mapper.order.ProductionDailyReportMapper;
import com.autoMotiveMes.service.business.DailyReportService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 实现功能【生产日报服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:13:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyReportServiceImpl implements DailyReportService {

    private final ProductionDailyReportMapper productionDailyReportMapper;
    private final EquipmentMapper equipmentMapper;

    @Override
    public DailyProductionSummaryDto getDailySummary(LocalDate date) {
        DailyProductionSummaryDto summary = productionDailyReportMapper.selectDailySummary(date);
        if (summary == null) {
            summary = new DailyProductionSummaryDto();
            summary.setReportDate(date);
        }

        // 计算设备利用率
        Integer utilization = equipmentMapper.selectDailyUtilization(date);
        summary.setEquipmentUtilization(utilization != null ? utilization : 0);

        return summary;
    }

    @Override
    public Page<DailyProductionDetailDto> getDailyDetails(LocalDate date, Integer page, Integer size) {
        Page<DailyProductionDetailDto> pageParam = new Page<>(page, size);
        return productionDailyReportMapper.selectDailyDetails(pageParam, date);
    }

    @Override
    public List<DailyOrderProgressDto> getOrderProgress(LocalDate date) {
        return productionDailyReportMapper.selectOrderProgress(date);
    }

    @Override
    public EquipmentDailyStatusDto getEquipmentStatusStats(LocalDate date) {
        return productionDailyReportMapper.selectEquipmentStatusStats();
    }
}