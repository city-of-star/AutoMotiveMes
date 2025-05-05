package com.autoMotiveMes.controller.equipment;

import com.autoMotiveMes.common.constant.CommonConstant;
import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.equipment.*;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.service.business.AlarmService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    private final RedisTemplate<String, EquipmentParameters> redisTemplate;

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

    @GetMapping("/historyData/{equipmentId}")
    public R<List<EquipmentParameters>> getHistoryData(@PathVariable Long equipmentId) {
        String redisKey = CommonConstant.REDIS_KEY_PREFIX + equipmentId;
        List<EquipmentParameters> data = redisTemplate.opsForList().range(redisKey, 0, -1);
        return R.success(data != null ? data : Collections.emptyList());
    }

    @GetMapping("/getEquipmentCount")
    public R<GetEquipmentCountResponseDto> getEquipmentCount() {
        return R.success(alarmService.getEquipmentCount());
    }
}