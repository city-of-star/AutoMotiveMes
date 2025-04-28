package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.dto.order.ProductionOrderDetailDto;
import com.autoMotiveMes.dto.order.ProductionOrderListDto;
import com.autoMotiveMes.dto.order.ProductionOrderQueryDto;
import com.autoMotiveMes.entity.order.ProductionOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【生产工单主表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:50:12
 */
@Mapper
public interface ProductionOrderMapper extends BaseMapper<ProductionOrder> {

    // 查询工单列表
    Page<ProductionOrderListDto> listOrders(Page<ProductionOrderListDto> page, @Param("dto") ProductionOrderQueryDto dto);

    // 获取工单详情
    ProductionOrderDetailDto getOrderDetail(Long orderId);

    // 查询当日最大工单流水号
    Integer selectMaxOrderSeq(String datePart);

    int countActiveReworkOrders(Long equipmentId);
}