package com.autoMotiveMes.service.impl.business;

import com.autoMotiveMes.common.exception.BusinessException;
import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.dto.order.SchedulePlanVo;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.order.ProcessDefinition;
import com.autoMotiveMes.entity.order.ProductionOrder;
import com.autoMotiveMes.entity.order.ProductionSchedule;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.mapper.order.ProcessDefinitionMapper;
import com.autoMotiveMes.mapper.order.ProductionOrderMapper;
import com.autoMotiveMes.mapper.order.ProductionScheduleMapper;
import com.autoMotiveMes.service.business.SchedulingService;
import com.autoMotiveMes.utils.CommonUtils;
import com.autoMotiveMes.service.simulationService.ProductionSimulationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 实现功能【排程服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-28 13:40:28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingServiceImpl implements SchedulingService {

    private final ProductionSimulationService productionSimulationService;

    private final ProductionScheduleMapper productionScheduleMapper;
    private final ProcessDefinitionMapper processDefinitionMapper;
    private final ProductionOrderMapper productionOrderMapper;
    private final EquipmentMapper equipmentMapper;

    @Override
    public Page<SchedulePlanVo> listSchedules(Long orderId, Integer page, Integer size) {
        Page<SchedulePlanVo> pageObj = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        return productionScheduleMapper.listSchedules(pageObj, orderId);
    }

    @Override
    public void generateSchedule(Long orderId) {
        // 1. 获取工单信息
        ProductionOrder order = productionOrderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 2) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION3);
        }

        // 2. 获取产品工序定义
        List<ProcessDefinition> processes = processDefinitionMapper.selectList(
                new QueryWrapper<ProcessDefinition>()
                        .eq("product_id", order.getProductId())
                        .orderByAsc("sequence")
        );

        // 3. 生成排程计划
        LocalDateTime currentTime = LocalDateTime.now();
        for (ProcessDefinition process : processes) {
            // 3.1 获取可用设备
            Equipment equipment = selectAvailableEquipment(process.getEquipmentType());
            if (equipment == null) {
                throw new BusinessException(ErrorCode.INVALID_OPERATION4, "工序["+process.getProcessName()+"]无可用设备");
            }

            // 3.2 计算时间窗口
            LocalDateTime[] timeWindow = calculateTimeWindow(
                    equipment.getEquipmentId(),
                    currentTime,
                    process.getStandardTime() * order.getOrderQuantity()
            );

            // 3.3 创建排程记录
            ProductionSchedule schedule = new ProductionSchedule();
            schedule.setOrderId(orderId);
            schedule.setProcessId(process.getProcessId());
            schedule.setEquipmentId(equipment.getEquipmentId());
            schedule.setPlannedStartTime(timeWindow[0]);
            schedule.setPlannedEndTime(timeWindow[1]);
            schedule.setScheduleStatus(1);
            schedule.setOperator(CommonUtils.getCurrentUserId());
            productionScheduleMapper.insert(schedule);

//            currentTime = timeWindow[1].plusMinutes(30);  // 留出换线时间(生产环境)
            currentTime = timeWindow[1].plusSeconds(1);  // 留出换线时间(测试环境)
        }

        // 启动模拟生产流程
        productionSimulationService.simulateProductionSequentially(orderId);
    }

    // 设备选择算法
    private Equipment selectAvailableEquipment(Integer equipmentType) {
        // 查询所有可用设备（状态正常且维护周期内）
        List<Equipment> equipments = equipmentMapper.selectList(
                new QueryWrapper<Equipment>()
                        .eq("equipment_type", equipmentType)
                        .eq("status", 1)
                        .apply("DATE_ADD(last_maintenance_date, INTERVAL maintenance_cycle DAY) >= CURDATE()")
        );

        if (equipments.isEmpty()) {
            return null;
        }

        // 选择负载最小的设备
        // 优先选择正在处理返工工单的设备
        return equipments.stream()
                .min(Comparator.comparingInt((Equipment e) -> {
                    // 优先考虑正在处理返工工单的设备
                    int reworkPriority = hasActiveReworkOrders(e.getEquipmentId()) ? 0 : 1;
                    return reworkPriority * 1000 + productionScheduleMapper.countRunningSchedules(e.getEquipmentId());
                }).thenComparing(Equipment::getEquipmentId)) // 添加次要排序条件
                .orElse(null);
    }

    private boolean hasActiveReworkOrders(Long equipmentId) {
        return productionOrderMapper.countActiveReworkOrders(equipmentId) > 0;
    }

    // 时间窗口计算
    private LocalDateTime[] calculateTimeWindow(Long equipmentId, LocalDateTime startTime, Integer totalSeconds) {
        // 获取设备最近排程
        ProductionSchedule lastSchedule = productionScheduleMapper.selectOne(
                new QueryWrapper<ProductionSchedule>()
                        .eq("equipment_id", equipmentId)
                        .orderByDesc("planned_end_time")
                        .last("LIMIT 1")
        );

        LocalDateTime baseTime = (lastSchedule != null &&
                lastSchedule.getPlannedEndTime().isAfter(startTime))
                ? lastSchedule.getPlannedEndTime()
                : startTime;

//        return new LocalDateTime[]{
//                baseTime.plusMinutes(30), // 设备准备时间(生产环境)
//                baseTime.plusSeconds(totalSeconds).plusMinutes(30)
//        };
        return new LocalDateTime[]{
                baseTime.plusSeconds(1), // 设备准备时间
                baseTime.plusSeconds(totalSeconds).plusSeconds(1)
        };
    }
}