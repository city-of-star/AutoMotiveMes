package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.entity.equipment.Equipment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
    @Select("select * from equipment where status = 1")
    List<Equipment> selectByEquipmentStatus();

    // 根据设备id获取涉笔信息
    @Select("select * from equipment where equipment_id = #{EquipmentId}")
    Equipment selectByEquipmentId(@Param("EquipmentId") Integer EquipmentId);

    // 获取正常状态的设备数量
    @Select("select count(1) from equipment where status = 1")
    Integer getNormalEquipmentCount();

    // 获取在线的设备数量
    @Select("select count(1) from equipment where status != 4")
    Integer getOnlineEquipmentCount();

    @Select("SELECT ROUND( " +
            "SUM(TIMESTAMPDIFF(SECOND, start_time, end_time)) / " +
            "(COUNT(DISTINCT equipment_id) * 86400) * 100 " +
            ") AS utilization " +
            "FROM equipment_status " +
            "WHERE status_code = 1 " +
            "AND DATE(start_time) = #{date}")
    Integer selectDailyUtilization(@Param("date") LocalDate date);
}