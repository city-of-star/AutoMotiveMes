package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.dto.equipment.MaintenanceRecordDetailVo;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListDto;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListVo;
import com.autoMotiveMes.entity.equipment.EquipmentMaintenance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【设备维护记录表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:11:59
 */
@Mapper
public interface EquipmentMaintenanceMapper extends BaseMapper<EquipmentMaintenance> {

    Page<MaintenanceRecordListVo> listMaintenanceRecord(Page<MaintenanceRecordListVo> page, @Param("dto") MaintenanceRecordListDto dto);

    MaintenanceRecordDetailVo selectMaintenanceDetailById(Long maintenanceId);
}