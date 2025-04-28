package com.autoMotiveMes.service.business;


import com.autoMotiveMes.dto.order.*;
import com.autoMotiveMes.entity.order.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 实现功能【工单服务】
 *
 * @author li.hongyu
 * @date 2025-04-23 21:19:17
 */
public interface OrderService {

    /**
     * 分页查询工单列表
     * @param dto 查询条件
     * @return 分页工单列表
     */
    Page<ProductionOrderListDto> listOrders(ProductionOrderQueryDto dto);

    /**
     * 根据工单id获取工单详情
     * @param orderId 工单id
     * @return 工单详情
     */
    ProductionOrderDetailDto getOrderDetail(Long orderId);

    /**
     * 创建工单
     * @param dto 工单信息
     */
    void createOrder(CreateProductionOrderDto dto);

    /**
     * 删除工单
     * @param orderId 工单id
     */
    void deleteOrder(Long orderId);

    /**
     * 更新工单状态（如果更新后为2，则自动排程）
     * @param orderId 工单id
     * @param status 工单状态
     */
    void updateOrderStatus(Long orderId, Integer status);

    /**
     * 获取产品列表
     * @return 产品列表
     */
    List<Product> listProduct();
}