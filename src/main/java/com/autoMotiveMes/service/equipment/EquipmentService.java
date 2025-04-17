package com.autoMotiveMes.service.equipment;

import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;

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
}