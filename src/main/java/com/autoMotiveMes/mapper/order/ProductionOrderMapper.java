package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.dto.order.ProductionOrderDetailDto;
import com.autoMotiveMes.dto.order.ProductionOrderListDto;
import com.autoMotiveMes.dto.order.ProductionOrderQueryDto;
import com.autoMotiveMes.entity.order.ProductionOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    Integer selectMaxOrderSeq(String datePart);

    @Select("SELECT COUNT(*) FROM production_order o " +
            "JOIN production_schedule s ON o.order_id = s.order_id " +
            "WHERE o.rework_of IS NOT NULL " +
            "AND s.equipment_id = #{equipmentId} " +
            "AND s.schedule_status IN (1, 2)")  // 1-待执行 2-执行中
    int countActiveReworkOrders(Long equipmentId);
}