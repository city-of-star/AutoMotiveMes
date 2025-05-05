package com.autoMotiveMes.service.business;

import com.autoMotiveMes.dto.equipment.MaintenanceRecordDetailResponseDto;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListRequestDto;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListResponseDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 实现功能【维护服务】
 *
 * @author li.hongyu
 * @date 2025-05-05 11:42:54
 */
public interface MaintenanceService {
    /**
     * 分页查询设备维护记录列表
     * @param dto 查询条件
     * @return 分页设备维护记录列表
     */
    Page<MaintenanceRecordListResponseDto> listMaintenanceRecord(MaintenanceRecordListRequestDto dto);

    /**
     * 通过id获取设备维护记录详情
     * @param maintenanceId 维护记录id
     * @return 设备维护记录详情
     */
    MaintenanceRecordDetailResponseDto getMaintenanceDetail(Long maintenanceId);
}