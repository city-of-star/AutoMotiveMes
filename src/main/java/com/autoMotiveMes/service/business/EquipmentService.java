package com.autoMotiveMes.service.business;

import com.autoMotiveMes.dto.equipment.*;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 实现功能【设备服务】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:16:21
 */
public interface EquipmentService {

    /**
     * 接受设备实时参数数据
     * @param data 设备实时参数数据
     */
    void acceptEquipmentRealTimeData(EquipmentParameters data);

    /**
     * 获取设备列表
     * @return 设备列表
     */
    List<Equipment> listEquipment();

    /**
     * 处理警报并产生维护记录
     * @param dto 处理过程记录
     */
    void handleAlarmMaintenance(HandleAlarmRequestDto dto);

    /**
     * 获取实时报警记录列表
     * @return 实时报警记录列表
     */
    List<RealTimeAlarmResponseDto> listRealTimeEquipmentAlarm();

    /**
     * 分页查询报警历史记录列表
     * @param query 查询条件
     * @return 分页报警历史记录列表
     */
    Page<AlarmHistoryResponseDto> listEquipmentAlarmHistory(AlarmHistoryRequestDto query);

    /**
     * 获取正常设备和在线设备的数量
     * @return 正常设备和在线设备的数量
     */
    GetEquipmentCountResponseDto getEquipmentCount();

    /**
     * 分页查询设备维护记录列表
     * @param dto 查询条件
     * @return 分页设备维护记录列表
     */
    Page<MaintenanceRecordListResponseDto> listMaintenanceRecord(MaintenanceRecordListRequestDto dto);

    /**
     * 通过id获取设备维护记录详情
     * @param maintenanceId 维护记录id
     * @return 设备维护记录详情
     */
    MaintenanceRecordDetailResponseDto getMaintenanceDetail(Long maintenanceId);
}