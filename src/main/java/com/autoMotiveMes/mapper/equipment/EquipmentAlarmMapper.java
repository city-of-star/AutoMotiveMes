package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.dto.equipment.AlarmHistoryDto;
import com.autoMotiveMes.dto.equipment.AlarmHistoryVo;
import com.autoMotiveMes.dto.equipment.RealTimeAlarmVo;
import com.autoMotiveMes.entity.equipment.EquipmentAlarm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【设备报警记录表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-15 23:11:38
 */
@Mapper
public interface EquipmentAlarmMapper extends BaseMapper<EquipmentAlarm> {

    // 获取实时报警记录
    List<RealTimeAlarmVo> listRealTimeEquipmentAlarm();

    // 获取历史报警记录
    Page<AlarmHistoryVo> listEquipmentAlarmHistory(Page<AlarmHistoryVo> page, @Param("dto") AlarmHistoryDto dto);
}