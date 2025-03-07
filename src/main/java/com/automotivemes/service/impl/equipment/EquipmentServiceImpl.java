package com.automotivemes.service.impl.equipment;

import com.automotivemes.common.dto.equipment.AlarmStatisticsDTO;
import com.automotivemes.common.dto.equipment.DailyReportDTO;
import com.automotivemes.common.dto.equipment.DailyReportQueryDTO;
import com.automotivemes.common.dto.equipment.HistoricalDataQueryDTO;
import com.automotivemes.entity.equipment.Equipment;
import com.automotivemes.entity.equipment.EquipmentRealtimeData;
import com.automotivemes.mapper.equipment.EquipmentRealtimeDataMapper;
import com.automotivemes.mapper.equipment.EquipmentMapper;
import com.automotivemes.service.equipment.EquipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMapper equipmentMapper;

    private final EquipmentRealtimeDataMapper realtimeDataMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    // 设备管理
    @Override
    public List<Equipment> getAllEquipment() {
        return equipmentMapper.selectList(null);
    }

    @Override
    public Equipment getEquipmentById(Integer id) {
        return equipmentMapper.selectById(id);
    }

    @Override
    public void addEquipment(Equipment equipment) {
        Date date = new Date();
        Equipment newEquipment = new Equipment();
        newEquipment.setEquipmentName(equipment.getEquipmentName());
        newEquipment.setType(equipment.getType());
        newEquipment.setStatus("待机");
        newEquipment.setLocation(equipment.getLocation());
        newEquipment.setOnlineTime(date);
        newEquipment.setLastMaintenance(date);
        equipmentMapper.insert(newEquipment);
    }

    @Override
    public void updateEquipmentStatus(Integer id, String status) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(id);
        equipment.setStatus(status);
        equipmentMapper.updateById(equipment);
    }

    // 实时数据管理
    @Override
    public Map<String, EquipmentRealtimeData> getRealtimeData() {
        Map<String, EquipmentRealtimeData> result = new HashMap<>();
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("equipment:realtime");

        entries.forEach((k, v) -> {
            try {
                String json = (String) v;
                EquipmentRealtimeData data = objectMapper.readValue(json, EquipmentRealtimeData.class);
                result.put((String) k, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    @Override
    public EquipmentRealtimeData getRealtimeDataByDeviceId(Integer EquipmentId) {
        String json = (String) redisTemplate.opsForHash().get("equipment:realtime", EquipmentId.toString());
        try {
            return objectMapper.readValue(json, EquipmentRealtimeData.class);
        } catch (Exception e) {
            return null;
        }
    }

//    // 历史数据查询
//    @Override
//    public List<EquipmentRealtimeData> getHistoricalData(HistoricalDataQueryDTO queryDTO) {
//        return realtimeDataMapper.selectHistoricalData(EquipmentId, startDate, endDate);
//    }
//
//    // 报警管理
//    @Override
//    public List<EquipmentRealtimeData> getActiveAlarms() {
//        return realtimeDataMapper.selectActiveAlarms();
//    }
//
//    // 统计报表
//    @Override
//    public List<DailyReportDTO> getDailyReport(DailyReportQueryDTO queryDTO) {
//        return realtimeDataMapper.selectDailyReport(startDate, endDate);
//    }
//
//    @Override
//    public List<AlarmStatisticsDTO> getAlarmStatistics() {
//        return realtimeDataMapper.selectAlarmStatistics();
//    }
}
