package com.automotivemes.service.impl.equipment;

import com.automotivemes.common.dto.equipment.AlarmStatisticsDTO;
import com.automotivemes.common.dto.equipment.DailyReportDTO;
import com.automotivemes.common.dto.equipment.DailyReportQueryDTO;
import com.automotivemes.common.dto.equipment.HistoricalDataQueryDTO;
import com.automotivemes.common.exception.EquipmentException;
import com.automotivemes.common.exception.ExceptionTypeEnum;
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

//    private final EquipmentRealtimeDataMapper realtimeDataMapper;

//    private final RedisTemplate<String, Object> redisTemplate;

//    private final ObjectMapper objectMapper;

    private final static List<String> validTypes = Arrays.asList("冲压", "焊接", "涂装", "总装");

    private final static List<String> validStatus = Arrays.asList("运行", "待机", "故障", "维护");

    // 设备管理
    @Override
    public Equipment addEquipment(Equipment equipment) {
        // 校验输入的合法性
        if (equipment.getEquipmentName() == null || equipment.getEquipmentName().isEmpty()) {
            throw new EquipmentException("设备名称不能为空", ExceptionTypeEnum.WARN);
        }
        if (equipmentMapper.existsByName(equipment.getEquipmentName())) {
            throw new EquipmentException("设备名称已存在: " + equipment.getEquipmentName(), ExceptionTypeEnum.WARN);
        }
        if (equipment.getType() == null || equipment.getType().isEmpty()) {
            throw new EquipmentException("设备类型不能为空", ExceptionTypeEnum.WARN);
        }
        if (!validTypes.contains(equipment.getType())) {
            throw new EquipmentException("无效的设备类型: " + equipment.getType(), ExceptionTypeEnum.WARN);
        }
        Equipment newEquipment = new Equipment();
        newEquipment.setEquipmentName(equipment.getEquipmentName());
        newEquipment.setType(equipment.getType());
        newEquipment.setStatus("待机");
        newEquipment.setLocation(equipment.getLocation());
        equipmentMapper.insert(newEquipment);
        return newEquipment;
    }

    @Override
    public void deleteEquipmentById(Integer id) {
        if (equipmentMapper.selectById(id) == null) {
            throw new EquipmentException("该设备已删除: " + id, ExceptionTypeEnum.WARN);
        }
        equipmentMapper.deleteById(id);
    }

    @Override
    public void updateEquipmentStatus(Integer id, String status) {
        if (equipmentMapper.selectById(id) == null) {
            throw new EquipmentException("该设备不存在: " + id, ExceptionTypeEnum.WARN);
        }
        if (!validStatus.contains(status)) {
            throw new EquipmentException("无效的设备状态: " + status, ExceptionTypeEnum.WARN);
        }
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(id);
        equipment.setStatus(status);
        equipmentMapper.updateById(equipment);
    }

    @Override
    public List<Equipment> getAllEquipment() {
        return equipmentMapper.selectList(null);
    }

    @Override
    public Equipment getEquipmentById(Integer id) {
        if (equipmentMapper.selectById(id) == null) {
            throw new EquipmentException("该设备不存在", ExceptionTypeEnum.WARN);
        }
        return equipmentMapper.selectById(id);
    }


//    // 实时数据管理
//    @Override
//    public Map<String, EquipmentRealtimeData> getRealtimeData() {
//        Map<String, EquipmentRealtimeData> result = new HashMap<>();
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries("equipment:realtime");
//
//        entries.forEach((k, v) -> {
//            try {
//                String json = (String) v;
//                EquipmentRealtimeData data = objectMapper.readValue(json, EquipmentRealtimeData.class);
//                result.put((String) k, data);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        return result;
//    }
//
//    @Override
//    public EquipmentRealtimeData getRealtimeDataByDeviceId(Integer EquipmentId) {
//        String json = (String) redisTemplate.opsForHash().get("equipment:realtime", EquipmentId.toString());
//        try {
//            return objectMapper.readValue(json, EquipmentRealtimeData.class);
//        } catch (Exception e) {
//            return null;
//        }
//    }

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
