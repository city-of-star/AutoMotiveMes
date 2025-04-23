package com.autoMotiveMes.service.impl.order;

import com.autoMotiveMes.dto.order.ListWorkOrderRequestDto;
import com.autoMotiveMes.dto.order.ListWorkOrderResponseDto;
import com.autoMotiveMes.dto.order.WorkOrderDetailResponseDto;
import com.autoMotiveMes.mapper.order.WorkOrderMapper;
import com.autoMotiveMes.service.order.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final WorkOrderMapper workOrderMapper;

    @Override
    public Page<ListWorkOrderResponseDto> listWorkOrders(ListWorkOrderRequestDto dto) {
        Page<ListWorkOrderResponseDto> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize());
        return workOrderMapper.listWorkOrders(page, dto);
    }

    @Override
    public WorkOrderDetailResponseDto getWorkOrderDetail(Long orderId) {
        return workOrderMapper.getWorkOrderDetail(orderId);
    }
}