package com.automotivemes.service.equipment;

import com.automotivemes.common.dto.equipment.AlarmStatisticsDTO;
import com.automotivemes.common.dto.equipment.DailyReportDTO;
import com.automotivemes.common.dto.equipment.DailyReportQueryDTO;
import com.automotivemes.common.dto.equipment.HistoricalDataQueryDTO;
import com.automotivemes.entity.equipment.Equipment;
import com.automotivemes.entity.equipment.EquipmentRealtimeData;

import java.util.List;
import java.util.Map;

public interface EquipmentService {

    // 设备管理
    Equipment addEquipment(Equipment equipment);
    void deleteEquipmentById(Integer id);
    void updateEquipmentStatus(Integer id, String status);
    Equipment getEquipmentById(Integer id);
    List<Equipment> getAllEquipment();




//    // 实时数据管理
//    java.util.Map<String, EquipmentRealtimeData> getRealtimeData();
//    EquipmentRealtimeData getRealtimeDataByDeviceId(Integer equipmentId);

//    // 历史数据查询
//    List<EquipmentRealtimeData> getHistoricalData(HistoricalDataQueryDTO queryDTO);
//
//    // 报警管理
//    List<EquipmentRealtimeData> getActiveAlarms();
//
//    // 统计报表
//    List<DailyReportDTO> getDailyReport(DailyReportQueryDTO queryDTO);
//    List<AlarmStatisticsDTO> getAlarmStatistics();
}