package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实现功能【设备运行参数记录表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:13:23
 */
@Mapper
public interface EquipmentParametersMapper extends BaseMapper<EquipmentParameters> {
    // 批量插入设备实时参数数据
    void insertBatch(@Param("list") List<EquipmentParameters> list);

    // 利用collect_time现有索引
    @Select("SELECT * FROM equipment_parameters " +
            "WHERE equipment_id = #{equipmentId} " +
            "AND collect_time >= #{start} AND collect_time < #{end} " +
            "ORDER BY collect_time ASC")
    List<EquipmentParameters> selectByEquipmentAndTime(
            @Param("equipmentId") Long equipmentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}