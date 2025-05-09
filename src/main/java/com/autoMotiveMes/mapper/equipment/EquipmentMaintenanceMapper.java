package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.dto.equipment.MaintenanceRecordDetailVo;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListDto;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListVo;
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
    Page<MaintenanceRecordListVo> listMaintenanceRecord(Page<MaintenanceRecordListVo> page, @Param("dto") MaintenanceRecordListDto dto);

    @Select("SELECT m.maintenance_id, m.maintenance_type, " +
            "DATE_FORMAT(m.plan_date, '%Y-%m-%d') AS plan_date, " +
            "DATE_FORMAT(m.actual_date, '%Y-%m-%d') AS actual_date, " +
            "m.maintenance_content, m.operator, m.result, m.cost, " +
            "e.equipment_code, e.equipment_name, e.equipment_model, " +
            "e.location, e.manufacturer, e.maintenance_cycle, " +
            "DATE_FORMAT(e.last_maintenance_date, '%Y-%m-%d') AS last_maintenance_date " +
            "FROM equipment_maintenance m " +
            "JOIN equipment e ON m.equipment_id = e.equipment_id " +
            "WHERE m.maintenance_id = #{maintenanceId}")
    MaintenanceRecordDetailVo selectMaintenanceDetailById(Long maintenanceId);
}