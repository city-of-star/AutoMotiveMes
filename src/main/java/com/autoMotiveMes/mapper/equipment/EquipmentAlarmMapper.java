package com.autoMotiveMes.mapper.equipment;

import com.autoMotiveMes.dto.equipment.AlarmHistoryRequestDto;
import com.autoMotiveMes.dto.equipment.AlarmHistoryResponseDto;
import com.autoMotiveMes.dto.equipment.RealTimeAlarmResponseDto;
import com.autoMotiveMes.entity.equipment.EquipmentAlarm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    // 获取实时报警记录
    List<RealTimeAlarmResponseDto> listRealTimeEquipmentAlarm();

    // 获取历史报警记录
    Page<AlarmHistoryResponseDto> listEquipmentAlarmHistory(Page<AlarmHistoryResponseDto> page, @Param("dto") AlarmHistoryRequestDto dto);
}