package com.autoMotiveMes.controller.business;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.order.DailyOrderProgressVo;
import com.autoMotiveMes.dto.order.DailyProductionDetailVo;
import com.autoMotiveMes.dto.order.DailyProductionSummaryVo;
import com.autoMotiveMes.dto.order.EquipmentDailyStatusVo;
import com.autoMotiveMes.service.business.DailyReportService;
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
 * 实现功能【生产日报 controller】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:18:10
 */
@RestController
@RequestMapping("/api/report/daily")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService reportService;

    @GetMapping("/summary")
    public R<DailyProductionSummaryVo> getDailySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return R.success(reportService.getDailySummary(date));
    }

    @GetMapping("/details")
    public R<Page<DailyProductionDetailVo>> getDailyDetails(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (date == null) date = LocalDate.now();
        return R.success(reportService.getDailyDetails(date, page, size));
    }

    @GetMapping("/order-progress")
    public R<List<DailyOrderProgressVo>> getOrderProgress(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return R.success(reportService.getOrderProgress(date));
    }

    @GetMapping("/equipment-status")
    public R<EquipmentDailyStatusVo> getEquipmentStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return R.success(reportService.getEquipmentStatusStats(date));
    }
}