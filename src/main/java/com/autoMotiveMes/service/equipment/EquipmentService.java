package com.autoMotiveMes.service.equipment;

import com.autoMotiveMes.entity.equipment.EquipmentParameters;

/**
 * 实现功能【设备接口】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:16:21
 */
public interface EquipmentService {

    void acceptEquipmentRealTimeData(EquipmentParameters data);
}