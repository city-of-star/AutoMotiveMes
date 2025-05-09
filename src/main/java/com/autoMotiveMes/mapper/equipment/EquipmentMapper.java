package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.entity.equipment.Equipment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 实现功能【设备基础信息表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:13:06
 */
@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {

    // 获取所有状态正常的设备
    List<Equipment> selectByEquipmentStatus();

    // 根据设备id获取设备信息
    Equipment selectByEquipmentId(@Param("EquipmentId") Integer EquipmentId);

    // 获取正常状态的设备数量
    Integer getNormalEquipmentCount();

    // 获取在线的设备数量
    Integer getOnlineEquipmentCount();

    Integer selectDailyUtilization(@Param("date") LocalDate date);
}