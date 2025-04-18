package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.entity.equipment.EquipmentMaintenance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 实现功能【设备维护记录表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:11:59
 */
@Mapper
public interface EquipmentMaintenanceMapper extends BaseMapper<EquipmentMaintenance> {
    @Select("SELECT * FROM equipment_maintenance WHERE equipment_id = #{equipmentId} " +
            "ORDER BY plan_date DESC LIMIT 1")
    EquipmentMaintenance selectLatestPlan(Long equipmentId);
}