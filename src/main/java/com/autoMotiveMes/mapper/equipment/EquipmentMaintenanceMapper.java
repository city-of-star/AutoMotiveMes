package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.dto.equipment.MaintenanceRecordListRequestDto;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListResponseDto;
import com.autoMotiveMes.entity.equipment.EquipmentMaintenance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    @Select("select m.maintenance_id, " +
            "e.equipment_code, " +
            "e.equipment_name, " +
            "m.maintenance_type, " +
            "m.plan_date, " +
            "m.actual_date, " +
            "m.operator, " +
            "m.result, " +
            "m.cost" +
            " from equipment_maintenance m " +
            "join equipment e on m.equipment_id = e.equipment_id ")
    Page<MaintenanceRecordListResponseDto> listMaintenanceRecord(Page<MaintenanceRecordListResponseDto> page, @Param("dto") MaintenanceRecordListRequestDto dto);
}