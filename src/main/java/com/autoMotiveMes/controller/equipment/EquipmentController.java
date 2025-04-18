package com.autoMotiveMes.controller.equipment;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentAlarm;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.service.equipment.EquipmentService;
import com.autoMotiveMes.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 实现功能【设备服务 controller】
 *
 * @author li.hongyu
 * @date 2025-04-16 16:40:20
 */
@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final RedisTemplate<String, EquipmentParameters> redisTemplate;

    @PostMapping("/acceptData")
    public void acceptEquipmentRealTimeData(@RequestBody EquipmentParameters data) {
        equipmentService.acceptEquipmentRealTimeData(data);
    }

    @GetMapping("/list")
    public R<List<Equipment>> listEquipment() {
        return R.success(equipmentService.listEquipment());
    }

    @GetMapping("/active-alarms")
    public R<List<EquipmentAlarm>> getActiveAlarms() {
        return R.success(equipmentService.listEquipmentAlarm());
    }

    @PostMapping("/handle-alarm")
    public R<?> handleAlarm(@RequestParam Long alarmId, @RequestParam String operator) {
        equipmentService.handleAlarmMaintenance(alarmId, operator);
        return R.successWithoutData();
    }

    @GetMapping("/historyData/{equipmentId}")
    public R<List<EquipmentParameters>> getHistoryData(@PathVariable Long equipmentId) {
        String redisKey = CommonUtils.REDIS_KEY_PREFIX + equipmentId;
        List<EquipmentParameters> data = redisTemplate.opsForList().range(redisKey, 0, -1);
        return R.success(data != null ? data : Collections.emptyList());
    }
}