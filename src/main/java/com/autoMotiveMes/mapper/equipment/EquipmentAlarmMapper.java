package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.entity.equipment.EquipmentAlarm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 实现功能【设备报警记录表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:11:38
 */
@Mapper
public interface EquipmentAlarmMapper extends BaseMapper<EquipmentAlarm> {
    @Select("SELECT * FROM equipment_alarm WHERE equipment_id = #{equipmentId} AND status = 0")
    List<EquipmentAlarm> selectActiveAlarms(Long equipmentId);
}