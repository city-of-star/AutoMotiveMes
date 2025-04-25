package com.autoMotiveMes.service.impl.order;


import com.autoMotiveMes.common.exception.ServerException;
import com.autoMotiveMes.dto.order.*;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.order.ProcessDefinition;
import com.autoMotiveMes.entity.order.Product;
import com.autoMotiveMes.entity.order.ProductionOrder;
import com.autoMotiveMes.entity.order.ProductionSchedule;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.mapper.order.*;
import com.autoMotiveMes.service.order.OrderService;
import com.autoMotiveMes.utils.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

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
            throw new ServerException("指定产品不存在");
        }

        // 校验时间顺序
        if (LocalDate.parse(dto.getPlannedStartDate())
                .isAfter(LocalDate.parse(dto.getPlannedEndDate()))) {
            throw new ServerException("计划开始时间不能晚于结束时间");
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
    public void updateOrderStatus(Long orderId, Integer status) {
        UpdateWrapper<ProductionOrder> wrapper = new UpdateWrapper<>();
        wrapper.set("status", status).eq("order_id", orderId);
        orderMapper.update(null, wrapper);

        if(status == 2) { // 已排程时自动生成排程计划
            generateSchedule(orderId);
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
            throw new ServerException("工单状态不合法或不存在");
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
                throw new ServerException("工序["+process.getProcessName()+"]无可用设备");
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

            currentTime = timeWindow[1].plusMinutes(30);  // 留出换线时间
        }
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

    // 设备选择算法
    private Equipment selectAvailableEquipment(Integer equipmentType) {
        // 查询所有可用设备（状态正常且维护周期内）
        List<Equipment> equipments = equipmentMapper.selectList(
                new QueryWrapper<Equipment>()
                        .eq("equipment_type", equipmentType)
                        .eq("status", 1)
                        .apply("DATE_ADD(last_maintenance_date, INTERVAL maintenance_cycle DAY) >= CURDATE()")
        );

        // 选择负载最小的设备（示例逻辑）
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

        return new LocalDateTime[]{
                baseTime.plusMinutes(30), // 设备准备时间
                baseTime.plusSeconds(totalSeconds).plusMinutes(30)
        };
    }
}