package com.autoMotiveMes.service.order;

import com.autoMotiveMes.dto.order.ListWorkOrderRequestDto;
import com.autoMotiveMes.dto.order.ListWorkOrderResponseDto;
import com.autoMotiveMes.dto.order.WorkOrderDetailResponseDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 实现功能【工单服务】
 *
 * @author li.hongyu
 * @date 2025-04-23 21:19:17
 */
public interface OrderService {

    // 获取及查询工单列表
    Page<ListWorkOrderResponseDto> listWorkOrders(ListWorkOrderRequestDto dto);

    // 获取工单详情
    WorkOrderDetailResponseDto getWorkOrderDetail(Long orderId);
}