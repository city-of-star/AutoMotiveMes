package com.autoMotiveMes.service.impl.order;


import com.autoMotiveMes.common.exception.BusinessException;
import com.autoMotiveMes.common.exception.ServerException;
import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.dto.order.*;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.order.*;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.mapper.order.*;
import com.autoMotiveMes.service.order.OrderService;
import com.autoMotiveMes.utils.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能【工单服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-23 21:23:51
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final QualityInspectionRecordMapper inspectionRecordMapper;
    private final QualityInspectionItemMapper inspectionItemMapper;
    private final ProductionRecordMapper recordMapper;
    private final ProductMapper productMapper;
    private final ProductionOrderMapper orderMapper;
    private final ProductionScheduleMapper scheduleMapper;
    private final ProcessDefinitionMapper processDefinitionMapper;
    private final EquipmentMapper equipmentMapper;

    @Override
    public Page<ProductionOrderListDto> listOrders(ProductionOrderQueryDto dto) {
        Page<ProductionOrderListDto> page = new Page<>(
                dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize()
        );
        return orderMapper.listOrders(page, dto);
    }

    @Override
    public ProductionOrderDetailDto getOrderDetail(Long orderId) {
        return orderMapper.getOrderDetail(orderId);
    }

    @Transactional
    @Override
    public void createOrder(CreateProductionOrderDto dto) {
        // 校验产品是否存在
        if (productMapper.selectById(dto.getProductId()) == null) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION1);
        }

        // 校验时间顺序
        if (LocalDate.parse(dto.getPlannedStartDate())
                .isAfter(LocalDate.parse(dto.getPlannedEndDate()))) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION2);
        }

        // 生成工单号（YYMMDD+4位流水）
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer maxSeq = orderMapper.selectMaxOrderSeq(datePart);
        String orderNo = datePart + String.format("%04d", (maxSeq == null ? 1 : maxSeq + 1));

        ProductionOrder order = new ProductionOrder();
        order.setOrderNo(orderNo);
        order.setProductId(dto.getProductId());
        order.setOrderQuantity(dto.getOrderQuantity());
        order.setPriority(dto.getPriority());
        order.setPlannedStartDate(LocalDate.parse(dto.getPlannedStartDate()));
        order.setPlannedEndDate(LocalDate.parse(dto.getPlannedEndDate()));
        order.setProductionLine(dto.getProductionLine());
        order.setCreator(CommonUtils.getCurrentUserId());
        order.setStatus(1); // 待排程

        orderMapper.insert(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        try {
            ProductionOrder order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(ErrorCode.INVALID_OPERATION5);
            }
            // 仅允许删除状态为待排程(1)或已关闭(6)的工单
            if (order.getStatus() != 1 && order.getStatus() != 6) {
                throw new BusinessException(ErrorCode.INVALID_OPERATION6);
            }
            // 存在此工单的排程计划，无法删除
            Long scheduleCount = scheduleMapper.selectCount(
                    new QueryWrapper<ProductionSchedule>().eq("order_id", orderId));
            if(scheduleCount > 0){
                throw new BusinessException(ErrorCode.INVALID_OPERATION7);
            }
            orderMapper.deleteById(orderId);
        } catch (ServerException e) {
            throw new ServerException("删除工单时出现错误 || " + e.getMessage());
        }
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        ProductionOrder order = orderMapper.selectOne(
                new QueryWrapper<ProductionOrder>().eq("order_id", orderId));
        Integer prevStatus = order.getStatus();  // 工单的原状态
        order.setStatus(status);
        orderMapper.updateById(order);

        if(status == 2) {  // 已排程时自动生成排程计划
            try {
                generateSchedule(orderId);
            } catch (BusinessException e) {
                // 如果排程失败，复原工单状态
                order.setStatus(prevStatus);
                orderMapper.updateById(order);
                throw e;
            }
        }
    }

    @Override
    public Page<SchedulePlanDto> listSchedules(Long orderId, Integer page, Integer size) {
        Page<SchedulePlanDto> pageObj = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        return scheduleMapper.listSchedules(pageObj, orderId);
    }

    @Override
    public void generateSchedule(Long orderId) {
        // 1. 获取工单信息
        ProductionOrder order = orderMapper.selectById(orderId);
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
            scheduleMapper.insert(schedule);

//            currentTime = timeWindow[1].plusMinutes(30);  // 留出换线时间(生产环境)
            currentTime = timeWindow[1].plusSeconds(1);  // 留出换线时间(测试环境)
        }

        // 启动模拟生产流程
        simulateProductionSequentially(orderId);
    }

    @Override
    public List<Product> listProduct() {
        return productMapper.selectList(null);
    }

    @Override
    public Page<ProductionRecordResponseDto> listProductionRecord(ProductionRecordQueryDTO dto) {
        Page<ProductionRecordResponseDto> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize());
        return recordMapper.listProductionRecord(page, dto);
    }

    @Override
    public Page<QualityTaskDto> listQualityTasks(QualityTaskQueryDto dto) {
        Page<QualityTaskDto> page = new Page<>(dto.getPage(), dto.getSize());
        return inspectionRecordMapper.selectQualityTasks(page, dto);
    }

    @Transactional
    @Override
    public void submitQualityResult(SubmitQualityResultDto dto) {
        QualityInspectionRecord record = inspectionRecordMapper.selectById(dto.getRecordId());

        // 更新质检记录
        record.setInspectionResult(dto.getInspectionResult());
        record.setInspectionData(dto.getInspectionData());
        record.setRemark(dto.getRemark());
        record.setInspectionTime(LocalDateTime.now());
        inspectionRecordMapper.updateById(record);

        // 检查所有质检项完成情况
        List<QualityInspectionRecord> records = inspectionRecordMapper.selectList(
                new QueryWrapper<QualityInspectionRecord>()
                        .eq("order_id", record.getOrderId())
                        .isNull("inspection_result")
        );

        if(records.isEmpty()) {
            ProductionOrder order = orderMapper.selectById(record.getOrderId());
            // 判断是否全部合格
            long unqualifiedCount = inspectionRecordMapper.selectUnqualifiedCount(order.getOrderId());
            order.setStatus(unqualifiedCount > 0 ? 4 : 5); // 4-需返工 5-已完成
            orderMapper.updateById(order);
        }
    }

    @Override
    public void generateQualityTasks(Long recordId) {
        ProductionRecord record = recordMapper.selectById(recordId);
        if(record == null || record.getQualityCheckGenerated() == 1) return;

        ProcessDefinition process = processDefinitionMapper.selectById(record.getProcessId());
        if(process == null || StringUtils.isBlank(process.getQualityCheckpoints())) return;

        try {
            List<String> checkpoints = new ObjectMapper()
                    .readValue(process.getQualityCheckpoints(), new TypeReference<>(){});

            checkpoints.forEach(itemName -> {
                QualityInspectionItem item = inspectionItemMapper.selectOne(
                        new QueryWrapper<QualityInspectionItem>()
                                .eq("inspection_name", itemName)
                                .eq("product_id", getProductIdByRecord(recordId))
                );

                if(item != null) {
                    createInspectionTask(record.getOrderId(), item.getItemId(), recordId);
                } else {
                    log.warn("未找到匹配的质检项: productId={}, itemName={}", process.getProductId(), itemName);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("质检项解析失败：{}", process.getQualityCheckpoints(), e);
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?") // 每1分钟检查一次
    public void checkCompletedProcess() {
        // 获取需要处理的记录（包含分页防止大数据量）
        int pageSize = 100;
        int page = 0;

        while(true) {
            Page<ProductionRecord> pageInfo = new Page<>(page, pageSize);
            List<ProductionRecord> records = recordMapper.selectUncheckedRecords(pageInfo);

            if(records.isEmpty()) break;

            records.forEach(record -> {
                try {
                    // 检查是否为末道工序
                    if(isLastProcess(record.getProcessId())) {
                        generateQualityTasks(record.getRecordId());
                    }

                    // 更新标记
                    record.setQualityCheckGenerated(1);
                    recordMapper.updateById(record);
                } catch (Exception e) {
                    log.error("处理记录[{}]失败: {}", record.getRecordId(), e.getMessage());
                }
            });

            page++;
        }
    }

    private void simulateProductionSequentially(Long orderId) {
        List<ProductionSchedule> schedules = scheduleMapper.selectList(
                new QueryWrapper<ProductionSchedule>()
                        .eq("order_id", orderId)
                        .orderByAsc("planned_start_time")
        );
        if (!schedules.isEmpty()) {
            // 更新工单状态为生产中
            ProductionOrder order = orderMapper.selectById(orderId);
            order.setStatus(3); // 生产中
            orderMapper.updateById(order);
            // 开始处理第一个工序
            processNextSchedule(schedules, 0);
        }
    }

    private void processNextSchedule(List<ProductionSchedule> schedules, int index) {
        if (index >= schedules.size()) {
            // 所有工序完成，更新工单状态
            ProductionOrder order = orderMapper.selectById(schedules.get(0).getOrderId());
            order.setStatus(5); // 已完成
            orderMapper.updateById(order);
            return;
        }

        ProductionSchedule schedule = schedules.get(index);
        // 更新实际开始时间和状态
        schedule.setActualStartTime(LocalDateTime.now());
        schedule.setScheduleStatus(2); // 执行中
        scheduleMapper.updateById(schedule);

        // 计算生产耗时
        ProcessDefinition process = processDefinitionMapper.selectById(schedule.getProcessId());
        ProductionOrder order = orderMapper.selectById(schedule.getOrderId());
        int totalSeconds = process.getStandardTime() * order.getOrderQuantity();

        // 模拟异步生产
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(totalSeconds); // 模拟生产耗时

                // 更新实际结束时间和状态
                schedule.setActualEndTime(LocalDateTime.now());
                schedule.setScheduleStatus(3); // 已完成
                scheduleMapper.updateById(schedule);

                // 生成生产记录
                ProductionRecord record = new ProductionRecord();
                record.setOrderId(order.getOrderId());
                record.setProcessId(schedule.getProcessId());
                record.setEquipmentId(schedule.getEquipmentId());
                record.setOutputQuantity(order.getOrderQuantity());
                record.setDefectiveQuantity(0);  // 假设无不良品
                record.setQualityCheckGenerated(0);
                record.setStartTime(schedule.getActualStartTime());
                record.setEndTime(schedule.getActualEndTime());
                record.setOperator(schedule.getOperator());
                recordMapper.insert(record);

                // 末道工序生成质检任务
                if (isLastProcess(schedule.getProcessId())) {
                    generateQualityTasks(record.getRecordId());
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

    private boolean isLastProcess(Long processId) {
        ProcessDefinition process = processDefinitionMapper.selectById(processId);
        Integer maxSequence = processDefinitionMapper.selectMaxSequence(process.getProductId());
        return process.getSequence().equals(maxSequence);
    }

    private Long getProductIdByRecord(Long recordId) {
        return Optional.ofNullable(orderMapper.selectById(
                        recordMapper.selectById(recordId).getOrderId()))
                .map(ProductionOrder::getProductId)
                .orElse(null);
    }

    private void createInspectionTask(Long orderId, Long itemId, Long recordId) {
        QualityInspectionRecord task = new QualityInspectionRecord();
        task.setOrderId(orderId);
        task.setItemId(itemId);
        task.setRecordId(recordId);
        task.setInspectionResult(3);  // 待复检
        task.setInspectionTime(LocalDateTime.now());
        task.setInspector(CommonUtils.getCurrentUserId());
        inspectionRecordMapper.insert(task);
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

        // 选择负载最小的设备
        return equipments.stream()
                .min(Comparator.comparingInt(e -> scheduleMapper.countRunningSchedules(e.getEquipmentId())))
                .orElse(null);
    }

    // 时间窗口计算
    private LocalDateTime[] calculateTimeWindow(Long equipmentId, LocalDateTime startTime, Integer totalSeconds) {
        // 获取设备最近排程
        ProductionSchedule lastSchedule = scheduleMapper.selectOne(
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