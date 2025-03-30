package com.automotivemes.service.impl.equipment;

import com.automotivemes.common.exception.GlobalException;
import com.automotivemes.entity.equipment.Equipment;
import com.automotivemes.mapper.equipment.EquipmentMapper;
import com.automotivemes.service.equipment.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMapper equipmentMapper;

    private final static List<String> validTypes = Arrays.asList("冲压", "焊接", "涂装", "总装");

    private final static List<String> validStatus = Arrays.asList("运行", "待机", "故障", "维护");

    // 设备管理
    @Override
    public Equipment addEquipment(Equipment equipment) {
        // 校验输入的合法性
        if (equipment.getEquipmentName() == null || equipment.getEquipmentName().isEmpty()) {
            throw new GlobalException("设备名称不能为空");
        }
        if (equipmentMapper.existsByName(equipment.getEquipmentName())) {
            throw new GlobalException("设备名称已存在: " + equipment.getEquipmentName());
        }
        if (equipment.getType() == null || equipment.getType().isEmpty()) {
            throw new GlobalException("设备类型不能为空");
        }
        if (!validTypes.contains(equipment.getType())) {
            throw new GlobalException("无效的设备类型: " + equipment.getType());
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
            throw new GlobalException("该设备已删除: " + id);
        }
        equipmentMapper.deleteById(id);
    }

    @Override
    public void updateEquipmentStatus(Integer id, String status) {
        if (equipmentMapper.selectById(id) == null) {
            throw new GlobalException("该设备不存在: " + id);
        }
        if (!validStatus.contains(status)) {
            throw new GlobalException("无效的设备状态: " + status);
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
            throw new GlobalException("该设备不存在");
        }
        return equipmentMapper.selectById(id);
    }

}
