package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.dto.order.ListWorkOrderRequestDto;
import com.autoMotiveMes.dto.order.ListWorkOrderResponseDto;
import com.autoMotiveMes.dto.order.WorkOrderDetailResponseDto;
import com.autoMotiveMes.entity.order.WorkOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【工单 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-23 21:06:59
 */
@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    Page<ListWorkOrderResponseDto> listWorkOrders(Page<ListWorkOrderResponseDto> page, @Param("dto") ListWorkOrderRequestDto dto);

    WorkOrderDetailResponseDto getWorkOrderDetail(@Param("orderId") Long orderId);
}