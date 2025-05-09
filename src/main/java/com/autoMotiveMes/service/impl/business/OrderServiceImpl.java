package com.autoMotiveMes.service.impl.business;


import com.autoMotiveMes.common.exception.BusinessException;
import com.autoMotiveMes.common.exception.ServerException;
import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.dto.order.*;
import com.autoMotiveMes.entity.order.*;
import com.autoMotiveMes.mapper.order.*;
import com.autoMotiveMes.service.business.OrderService;
import com.autoMotiveMes.service.business.SchedulingService;
import com.autoMotiveMes.utils.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private final SchedulingService schedulingService;

    private final ProductMapper productMapper;
    private final ProductionOrderMapper orderMapper;
    private final ProductionScheduleMapper scheduleMapper;

    @Override
    public Page<ProductionOrderListVo> listOrders(ProductionOrderQueryDto dto) {
        Page<ProductionOrderListVo> page = new Page<>(
                dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize()
        );
        return orderMapper.listOrders(page, dto);
    }

    @Override
    public ProductionOrderDetailVo getOrderDetail(Long orderId) {
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
                schedulingService.generateSchedule(orderId);
            } catch (BusinessException e) {
                // 如果排程失败，复原工单状态
                order.setStatus(prevStatus);
                orderMapper.updateById(order);
                throw e;
            }
        }
    }

    @Override
    public List<Product> listProduct() {
        return productMapper.selectList(null);
    }

//    @Override
//    public ProductionOrder getOriginalOrder(Long reworkOrderId) {
//        return orderMapper.selectOne(new QueryWrapper<ProductionOrder>()
//                .eq("rework_of", reworkOrderId));
//    }
}