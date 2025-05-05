package com.autoMotiveMes.service.impl.business;

import com.autoMotiveMes.dto.equipment.MaintenanceRecordDetailResponseDto;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListRequestDto;
import com.autoMotiveMes.dto.equipment.MaintenanceRecordListResponseDto;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentMaintenance;
import com.autoMotiveMes.mapper.equipment.EquipmentMaintenanceMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.service.business.MaintenanceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 实现功能【维护服务类】
 *
 * @author li.hongyu
 * @date 2025-05-05 11:43:08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {

    private final EquipmentMapper equipmentMapper;
    private final EquipmentMaintenanceMapper maintenanceMapper;

    @Scheduled(cron = "0 0 8 * * ?") // 每天8点生成计划
    public void generatePreventiveMaintenance() {
        List<Equipment> equipments = equipmentMapper.selectList(
                new QueryWrapper<Equipment>().isNotNull("maintenance_cycle"));

        equipments.forEach(equip -> {
            EquipmentMaintenance maintenance = new EquipmentMaintenance();
            maintenance.setEquipmentId(equip.getEquipmentId());
            maintenance.setPlanDate(LocalDate.now().plusDays(equip.getMaintenanceCycle()));
            maintenance.setMaintenanceType(1);  // 预防性
            maintenance.setMaintenanceContent("定期保养");

            maintenanceMapper.insert(maintenance);
        });
    }

    @Override
    public Page<MaintenanceRecordListResponseDto> listMaintenanceRecord(MaintenanceRecordListRequestDto dto) {
        Page<MaintenanceRecordListResponseDto> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize());
        return maintenanceMapper.listMaintenanceRecord(page, dto);
    }

    @Override
    public MaintenanceRecordDetailResponseDto getMaintenanceDetail(Long maintenanceId) {
        return maintenanceMapper.selectMaintenanceDetailById(maintenanceId);
    }
}