package com.autoMotiveMes.controller.business;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordDetailResponseDto;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListRequestDto;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListResponseDto;
import com.autoMotiveMes.service.business.MaintenanceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【维护服务 controller】
 *
 * @author li.hongyu
 * @date 2025-05-05 11:50:21
 */
@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping("/listMaintenanceRecord")
    public R<Page<MaintenanceRecordListResponseDto>> listMaintenanceRecord(@RequestBody MaintenanceRecordListRequestDto dto) {
        Page<MaintenanceRecordListResponseDto> page = maintenanceService.listMaintenanceRecord(dto);
        return R.success(page);
    }

    @GetMapping("/maintenanceDetail/{maintenanceId}")
    public R<MaintenanceRecordDetailResponseDto> getMaintenanceDetail(@PathVariable Long maintenanceId) {
        return R.success(maintenanceService.getMaintenanceDetail(maintenanceId));
    }
}