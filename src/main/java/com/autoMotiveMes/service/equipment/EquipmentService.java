package com.autoMotiveMes.service.equipment;

import com.autoMotiveMes.dto.equipment.AlarmHistoryRequestDto;
import com.autoMotiveMes.dto.equipment.AlarmHistoryResponseDto;
import com.autoMotiveMes.dto.equipment.HandleAlarmRequestDto;
import com.autoMotiveMes.dto.equipment.RealTimeAlarmResponseDto;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentAlarm;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 实现功能【设备接口】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:16:21
 */
public interface EquipmentService {

    // 接受设备实时参数数据
    void acceptEquipmentRealTimeData(EquipmentParameters data);

    // 查询设备列表
    List<Equipment> listEquipment();

    // 处理警报并产生维护记录
    void handleAlarmMaintenance(HandleAlarmRequestDto dto);

    // 获取实时警报
    List<RealTimeAlarmResponseDto> listRealTimeEquipmentAlarm();

    // 获取警报历史
    Page<AlarmHistoryResponseDto> listEquipmentAlarmHistory(AlarmHistoryRequestDto query);
}