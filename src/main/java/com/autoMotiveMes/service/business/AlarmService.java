package com.autoMotiveMes.service.business;

import com.autoMotiveMes.dto.equipment.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 实现功能【报警服务】
 *
 * @author li.hongyu
 * @date 2025-05-05 11:36:43
 */
public interface AlarmService {
    /**
     * 处理警报并产生维护记录
     * @param dto 处理过程记录
     */
    void handleAlarmMaintenance(HandleAlarmDto dto);

    /**
     * 获取实时报警记录列表
     * @return 实时报警记录列表
     */
    List<RealTimeAlarmVo> listRealTimeEquipmentAlarm();

    /**
     * 分页查询报警历史记录列表
     * @param query 查询条件
     * @return 分页报警历史记录列表
     */
    Page<AlarmHistoryVo> listEquipmentAlarmHistory(AlarmHistoryDto query);

    /**
     * 获取正常设备和在线设备的数量
     * @return 正常设备和在线设备的数量
     */
    GetEquipmentCountVo getEquipmentCount();
}