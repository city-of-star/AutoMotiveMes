package com.autoMotiveMes.controller.business;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.equipment.*;
import com.autoMotiveMes.service.business.AlarmService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现功能【报警服务 controller】
 *
 * @author li.hongyu
 * @date 2025-05-05 11:50:10
 */
@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/listRealTimeAlarms")
    public R<List<RealTimeAlarmResponseDto>> listRealTimeEquipmentAlarm() {
        return R.success(alarmService.listRealTimeEquipmentAlarm());
    }

    @PostMapping("/listEquipmentAlarmHistory")
    public R<Page<AlarmHistoryResponseDto>> listEquipmentAlarmHistory(@RequestBody AlarmHistoryRequestDto dto) {
        return R.success(alarmService.listEquipmentAlarmHistory(dto));
    }

    @PostMapping("/handleAlarm")
    public R<?> handleAlarm(@RequestBody HandleAlarmRequestDto dto) {
        alarmService.handleAlarmMaintenance(dto);
        return R.success();
    }

    @GetMapping("/getEquipmentCount")
    public R<GetEquipmentCountResponseDto> getEquipmentCount() {
        return R.success(alarmService.getEquipmentCount());
    }
}