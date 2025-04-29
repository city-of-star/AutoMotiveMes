package com.autoMotiveMes.controller.order;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.order.DailyOrderProgressDto;
import com.autoMotiveMes.dto.order.DailyProductionDetailDto;
import com.autoMotiveMes.dto.order.DailyProductionSummaryDto;
import com.autoMotiveMes.dto.order.EquipmentDailyStatusDto;
import com.autoMotiveMes.service.business.ProductionDailyReportService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 实现功能【】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:18:10
 */
@RestController
@RequestMapping("/api/report/daily")
@RequiredArgsConstructor
public class DailyReportController {

    private final ProductionDailyReportService reportService;

    @GetMapping("/summary")
    public R<DailyProductionSummaryDto> getDailySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return R.success(reportService.getDailySummary(date));
    }

    @GetMapping("/details")
    public R<Page<DailyProductionDetailDto>> getDailyDetails(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (date == null) date = LocalDate.now();
        return R.success(reportService.getDailyDetails(date, page, size));
    }

    @GetMapping("/order-progress")
    public R<List<DailyOrderProgressDto>> getOrderProgress(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return R.success(reportService.getOrderProgress(date));
    }

    @GetMapping("/equipment-status")
    public R<EquipmentDailyStatusDto> getEquipmentStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return R.success(reportService.getEquipmentStatusStats(date));
    }
}