package com.autoMotiveMes.service.simulationService;

import com.autoMotiveMes.entity.order.ProcessDefinition;
import com.autoMotiveMes.entity.order.ProductionOrder;
import com.autoMotiveMes.entity.order.ProductionRecord;
import com.autoMotiveMes.entity.order.ProductionSchedule;
import com.autoMotiveMes.mapper.order.ProcessDefinitionMapper;
import com.autoMotiveMes.mapper.order.ProductionOrderMapper;
import com.autoMotiveMes.mapper.order.ProductionRecordMapper;
import com.autoMotiveMes.mapper.order.ProductionScheduleMapper;
import com.autoMotiveMes.service.business.QualityInspectionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能【生产模拟服务】
 *
 * @author li.hongyu
 * @date 2025-04-28 13:51:06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionSimulationService {

    private final QualityInspectionService qualityInspectionService;

    private final ProductionOrderMapper productionOrderMapper;
    private final ProductionRecordMapper productionRecordMapper;
    private final ProductionScheduleMapper productionScheduleMapper;
    private final ProcessDefinitionMapper processDefinitionMapper;

    public void simulateProductionSequentially(Long orderId) {
        List<ProductionSchedule> schedules = productionScheduleMapper.selectList(
                new QueryWrapper<ProductionSchedule>()
                        .eq("order_id", orderId)
                        .orderByAsc("planned_start_time")
        );
        if (!schedules.isEmpty()) {
            // 更新工单状态为生产中
            ProductionOrder order = productionOrderMapper.selectById(orderId);
            order.setStatus(3); // 生产中
            productionOrderMapper.updateById(order);
            // 开始处理第一个工序
            processNextSchedule(schedules, 0);
        }
    }

    private void processNextSchedule(List<ProductionSchedule> schedules, int index) {
        productionScheduleMapper.flushStatusUpdates();

        if (index >= schedules.size()) {
            // 所有工序完成，更新工单状态
            ProductionOrder order = productionOrderMapper.selectById(schedules.get(0).getOrderId());
            order.setStatus(5); // 已完成
            productionOrderMapper.updateById(order);
            return;
        }

        ProductionSchedule schedule = schedules.get(index);
        // 更新实际开始时间和状态
        schedule.setActualStartTime(LocalDateTime.now());
        schedule.setScheduleStatus(2); // 执行中
        productionScheduleMapper.updateById(schedule);

        // 计算生产耗时
        ProcessDefinition process = processDefinitionMapper.selectById(schedule.getProcessId());
        ProductionOrder order = productionOrderMapper.selectById(schedule.getOrderId());
        int totalSeconds = process.getStandardTime() * order.getOrderQuantity();

        // 模拟异步生产
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(totalSeconds); // 模拟生产耗时

                // 更新实际结束时间和状态
                schedule.setActualEndTime(LocalDateTime.now());
                schedule.setScheduleStatus(3); // 已完成
                productionScheduleMapper.updateById(schedule);

                // 生成生产记录
                ProductionRecord record = new ProductionRecord();
                record.setOrderId(order.getOrderId());
                record.setProcessId(schedule.getProcessId());
                record.setEquipmentId(schedule.getEquipmentId());
                record.setOutputQuantity(order.getOrderQuantity());

                // 根据工序类型生成不良品数量
                int defectiveQty = calculateDefectiveQuantity(order, process);
                record.setDefectiveQuantity(defectiveQty);
                // 更新工单累计不良品数量和完工量
                order.setDefectiveQuantity(order.getDefectiveQuantity() + defectiveQty);
                order.setCompletedQuantity(order.getCompletedQuantity() + record.getOutputQuantity());
                productionOrderMapper.updateById(order);

                record.setQualityCheckGenerated(0);
                record.setStartTime(schedule.getActualStartTime());
                record.setEndTime(schedule.getActualEndTime());
                record.setOperator(schedule.getOperator());
                productionRecordMapper.insert(record);

                // 末道工序生成质检任务
                if (isLastProcess(schedule.getProcessId())) {
                    qualityInspectionService.generateQualityTasks(record.getRecordId());
                }

                // 处理下一工序
                processNextSchedule(schedules, index + 1);
            } catch (InterruptedException e) {
                log.error("生产模拟中断: {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("生产记录生成失败: {}", e.getMessage());
            }
        });
    }

    // 不良品计算逻辑
    private int calculateDefectiveQuantity(ProductionOrder order, ProcessDefinition process) {
        // 获取工序基准不良率
        DefectRateRange rateRange = getDefectRateRange(process);

        // 生成随机不良率（正态分布）
        double defectRate = ThreadLocalRandom.current().nextDouble(rateRange.minRate, rateRange.maxRate);
        defectRate = Math.max(rateRange.minRate, Math.min(defectRate, rateRange.maxRate));

        // 计算不良数量
        int defective = (int) Math.round(order.getOrderQuantity() * defectRate);

        // 保证有效范围
        return Math.max(0, Math.min(defective, order.getOrderQuantity()));
    }

    // 定义不同工序类型的不良率范围
    private DefectRateRange getDefectRateRange(ProcessDefinition process) {
        // 汽车行业典型工序不良率基准
        String processName = process.getProcessName();

        if (processName.contains("压铸")) {
            return new DefectRateRange(0.005, 0.015); // 0.5%-1.5%
        } else if (processName.contains("焊接")) {
            return new DefectRateRange(0.002, 0.008); // 0.2%-0.8%
        } else if (processName.contains("装配")) {
            return new DefectRateRange(0.001, 0.005); // 0.1%-0.5%
        } else if (processName.contains("测试")) {
            return new DefectRateRange(0.0005, 0.002); // 0.05%-0.2%
        } else {
            return new DefectRateRange(0.001, 0.01); // 默认0.1%-1%
        }
    }

    // 是否是最后一道工序
    private boolean isLastProcess(Long processId) {
        ProcessDefinition process = processDefinitionMapper.selectById(processId);
        Integer maxSequence = processDefinitionMapper.selectMaxSequence(process.getProductId());
        return process.getSequence().equals(maxSequence);
    }

    // 不良率范围值对象
    @AllArgsConstructor
    private static class DefectRateRange {
        double minRate;
        double maxRate;
    }
}