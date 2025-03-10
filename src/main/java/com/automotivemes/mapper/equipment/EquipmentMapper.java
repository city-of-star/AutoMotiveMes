package com.automotivemes.mapper.equipment;

import com.automotivemes.entity.equipment.Equipment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {
    @Select("SELECT EXISTS (SELECT 1 FROM equipment WHERE equipment_name = #{equipmentName})")
    boolean existsByName(String equipmentName);
}
