package com.autoMotiveMes.service.business;

import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;

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
}