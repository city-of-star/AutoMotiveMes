package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.entity.equipment.EquipmentType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 实现功能【设备类型表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:13:59
 */
@Mapper
public interface EquipmentTypeMapper extends BaseMapper<EquipmentType> {

    // 获取状态正常的设备的类型
    @Select("select * from equipment_type")
    List<EquipmentType> listEquipmentType();
}